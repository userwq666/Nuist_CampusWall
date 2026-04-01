package com.nuist_campuswall.service.file.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.enums.FileBizType;
import com.nuist_campuswall.domain.enums.FileStatus;
import com.nuist_campuswall.domain.enums.FileType;
import com.nuist_campuswall.domain.file.FileAsset;
import com.nuist_campuswall.mapper.file.FileAssetMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileAssetMapper fileAssetMapper;

    //允许的图片后缀
    private static final Set<String> ALLOWED_IMAGE_EXT = Set.of("jpg","jpeg","png","webp","gif");

    //允许的图片MIME类型
    private static final Set<String> ALLOWED_IMAGE_MIME = Set.of(
            "image/jpeg","image/png","image/webp","image/gif"
    );

    //文件最大大小5MB
    @Value("${app.file.max-image-size}")
    private long maxSize;            //由配置文件获取

    //文件存储根目录
    @Value("${app.file.local-root:upload}")
    private String localRoot;

    //访问路径前缀
    @Value("${app.file.url-prefix:/static/upload}")
    private String urlPrefix;




    //--------------文件上传接口实现------------------
    @Override
    public Long upload(MultipartFile file, FileType fileType) {

        Long userId = UserContext.getUserId();

        //1.检测文件存在
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "文件不能为空");
        }
        if(fileType == null){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "文件类型不能为空");
        }

        //2.检测当前用户
        if(userId == null){
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或token缺失");
        }

        //3.检测文件类型和大小
        String ext = getFileExt(file.getOriginalFilename());
        String mime = file.getContentType();

        if (!ALLOWED_IMAGE_EXT.contains(ext) || mime == null || !ALLOWED_IMAGE_MIME.contains(mime.toLowerCase())) {
            throw new BusinessException(ErrorCode.FILE_TYPE_ERROR, "仅支持jpg/jpeg/png/webp/gif图片格式");
        }

        if (file.getSize() > maxSize) {
            throw new BusinessException(ErrorCode.FILE_SIZE_ERROR, "文件大小不能超过5MB");
        }


        //4.组装文件实体
        FileAsset fileAsset = new FileAsset();
        fileAsset.setOwnerUserId(userId);                            //当前用户ID
        fileAsset.setFileType(fileType);                            //文件类型
        fileAsset.setOriginalName(file.getOriginalFilename());     //文件原始名称
        fileAsset.setMimeType(mime);                              //文件MIME类型
        fileAsset.setFileExt(ext);                               //文件后缀
        fileAsset.setStatus(FileStatus.TEMP);                   //文件状态
        fileAsset.setFileSize(file.getSize());                 //文件大小

        //5.构建存储路径
        String objectKey = buildObjectKey(fileType, userId, ext);
        java.nio.file.Path target = java.nio.file.Paths.get(localRoot, objectKey);      //java.nio.file.Paths 是构建路径的工具类
        
        try {
            java.nio.file.Files.createDirectories(target.getParent());                           //创建目录，如果目录已存在则不会报错，如果目录不存在则会创建
            file.transferTo(target.toFile());                                                   //将文件保存到目标路径，如果文件已存在则会覆盖
        } catch (java.io.IOException e) {
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件保存失败");
        }

        String url = urlPrefix + "/" + objectKey.replace("\\", "/");
        fileAsset.setObjectKey(objectKey);
        fileAsset.setUrl(url);

        //6. 入库
        fileAssetMapper.insert(fileAsset);
        //7. 返回文件ID
        return fileAsset.getId();
    }

    //--------------文件绑定业务接口实现------------------
    @Override
    public void bindFileToBiz(Long fileId, FileType fileType, Long bizId) {
       //1.参数检测
        if(fileId == null|| fileType == null|| bizId == null){
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "参数不能为空");
        }

        //2.登录校验
        Long userId = UserContext.getUserId();
        if(userId == null){
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或token缺失");
        }

        //3.文件存在校验
        FileAsset fileAsset = fileAssetMapper.selectById(fileId);
        if(fileAsset == null){
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, "文件不存在");
        }

        //4.文件类型校验
        if(!fileAsset.getFileType().equals(fileType)){
            throw new BusinessException(ErrorCode.FILE_TYPE_ERROR, "文件类型不匹配");
        }

        //5.文件状态校验
        if(!fileAsset.getStatus().equals(FileStatus.TEMP)){
            throw new BusinessException(ErrorCode.FILE_STATUS_ERROR, "当前文件状态不允许绑定");
        }

        //6.所有权限校验
        if(!fileAsset.getOwnerUserId().equals(userId)){
            throw new BusinessException(ErrorCode.NO_PERMISSION, "无权限操作");
        }

        //7.文件绑定
        String oldObjectKey = fileAsset.getObjectKey();
        String newObjectKey = buildBizObjectKey(fileType, bizId, oldObjectKey);    //构建新的存储路径

        Path source = Paths.get(localRoot, oldObjectKey);
        Path target = Paths.get(localRoot, newObjectKey);

        try {
            Files.createDirectories(target.getParent());                           //创建目录，如果目录已存在则不会报错，如果目录不存在则会创建
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);    //文件迁移 standardCopyOption.REPLACE_EXISTING表示如果目标文件已存在则覆盖
        }catch (NoSuchFileException e){
            throw new BusinessException(ErrorCode.FILE_NOT_FOUND, "文件不存在");
        }catch (IOException e){
            throw new BusinessException(ErrorCode.FILE_UPLOAD_ERROR, "文件迁移失败");
        }

        //8.更新绑定信息
        FileAsset updateFile = new FileAsset();
        updateFile.setId(fileId);
        updateFile.setBizId(bizId);
        updateFile.setBizType(toBizType(fileType));
        updateFile.setStatus(FileStatus.BOUND);
        updateFile.setObjectKey(newObjectKey);
        updateFile.setUrl(urlPrefix + "/" + newObjectKey.replace("\\", "/"));

        //9.更新数据库
        fileAssetMapper.updateById(updateFile);
    }

    //----------------文件删除接口实现(业务)------------------
   @Override
    public void markTempByBiz(FileType fileType, Long bizId) {
        //1.参数检测
        if(bizId == null|| fileType == null){
            return;
        }

        //2.fileType-->bizType
        FileBizType bizType = toBizType(fileType);

        //3.查出该业务下所有未删除文件
       List<FileAsset> list = fileAssetMapper.selectList(
               Wrappers.<FileAsset>lambdaQuery()
               .eq(FileAsset::getBizType, bizType)
               .eq(FileAsset::getBizId, bizId)
               .eq(FileAsset::getStatus, FileStatus.BOUND)
       );

       //4.删除(修改为temp)所有文件
        for (FileAsset item : list) {
            FileAsset updateFile = new FileAsset();
            updateFile.setId(item.getId());
            updateFile.setStatus(FileStatus.TEMP);
            updateFile.setBizType(null);
            updateFile.setBizId(null);
            fileAssetMapper.updateById(updateFile);
        }
    }

    //----------------文件删除接口实现(物理)------------------
    @Override
    public void deleteFileById(Long fileId) {
        //1.参数检测
        if(fileId == null){
            return;
        }

        //2.file存在校验
        FileAsset fileAsset = fileAssetMapper.selectById(fileId);
        if(fileAsset == null){
            return;
        }

        //3.文件存在校验
        String objectKey = fileAsset.getObjectKey();
        if(objectKey != null && !objectKey.isBlank()){
            try {
                Path path = Paths.get(localRoot, objectKey);
                Files.deleteIfExists(path);
            }catch (IOException e){
                throw new BusinessException(ErrorCode.FILE_DELETE_ERROR, "文件删除失败");
            }
        }

        //4.删除数据库记录
        FileAsset updateFile = new FileAsset();
        updateFile.setId(fileId);
        updateFile.setStatus(FileStatus.DELETED);       //文件状态删除
        fileAssetMapper.updateById(updateFile);
    }


    //----------------私有方法(FileType to FileBizType)------------------
    private FileBizType toBizType(FileType fileType) {
        return switch (fileType) {
            case POST -> FileBizType.POST;
            case COMMENT -> FileBizType.COMMENT;
            case AVATAR -> FileBizType.AVATAR;
        };
    }

    //----------------私有方法(获取文件后缀)------------------
    private String getFileExt(String originalName) {
        if (originalName == null || originalName.isBlank() || !originalName.contains(".")) {
            return "";
        }
        return originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
    }

    //----------------私有方法(重命名图片)------------------
    private String buildStoredFilename(Long userId, String ext) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return userId + "_" + System.currentTimeMillis() + "_" + uuid + "." + ext;    //id+时间戳+uuid+扩展名
    }

    //----------------私有方法(生成对象路径)------------------
    private String buildObjectKey(FileType fileType,Long userId, String ext) {
        String datePath = java.time.LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        //先统一存储到temp目录下
        String fileName = buildStoredFilename(userId, ext);
        return "temp/"+datePath+"/"+fileName;
    }

    //----------------私有方法(按文件类型映射业务目录)------------------
    private String resolveBizDir(FileType fileType) {
        return switch (fileType) {
            case POST -> "post";
            case COMMENT -> "comment";
            case AVATAR -> "avatar";
        };
    }

    //----------------私有方法(temp路径转业务路径)------------------
    private String buildBizObjectKey(FileType fileType, Long bizId, String oldObjectKey) {
        String fileName = Paths.get(oldObjectKey).getFileName().toString();
        return resolveBizDir(fileType) + "/" + bizId + "/" + fileName;
    }
}

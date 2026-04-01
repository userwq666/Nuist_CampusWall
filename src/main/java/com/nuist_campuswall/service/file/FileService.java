package com.nuist_campuswall.service.file;

import com.nuist_campuswall.domain.enums.FileType;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    //上传文件接口        MultipartFile是spring提供的文件上传接口, file是前端上传的文件
    Long upload(MultipartFile file, FileType fileType);

    //绑定文件到业务接口
    void bindFileToBiz(Long fileId, FileType fileType, Long bizId);

    //文件删除接口(物理)
    void deleteFileById(Long fileId);

    //文件删除接口(业务)
    void markTempByBiz(FileType fileType, Long bizId);
}

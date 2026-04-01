package com.nuist_campuswall.domain.file;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nuist_campuswall.domain.enums.FileBizType;
import com.nuist_campuswall.domain.enums.FileStatus;
import com.nuist_campuswall.domain.enums.FileType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("file_asset")
public class FileAsset {
    //文件ID
    @TableId(type = IdType.AUTO)
    private Long id;
    //文件拥有者
    private Long ownerUserId;
    //文件类型
    private FileType fileType;
    //业务类型
    private FileBizType bizType;
    //业务ID
    private Long bizId;
    //原始文件名
    private String originalName;
    //对象路径
    private String objectKey;
    //文件地址
    private String url;
    //文件MIME类型
    private String mimeType;
    //文件后缀
    private String fileExt;
    //文件摘要
    private String sha256;
    //文件大小
    private Long fileSize;
    //文件状态
    private FileStatus status;
    //创建时间
    private LocalDateTime createTime;
    //更新时间
    private LocalDateTime updateTime;
}

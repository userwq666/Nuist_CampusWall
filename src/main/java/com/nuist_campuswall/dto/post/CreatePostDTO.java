package com.nuist_campuswall.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//发帖DTO
@Data
public class CreatePostDTO {
    // 帖子标题
    @NotBlank(message = "标题不能为空")         //NOT_BLANK注解表示字段不能为空
    @Size(max = 50, message = "标题长度不能超过50")
    private String title;
    // 帖子正文
    @NotBlank(message = "正文不能为空")
    @Size(max = 10000, message = "正文长度不能超过10000")
    private String content;
    // 图片链接
    private String imageUrl;
    //图片ID
    private Long fileID;
}

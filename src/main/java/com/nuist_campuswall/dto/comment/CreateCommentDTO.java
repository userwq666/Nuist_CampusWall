package com.nuist_campuswall.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateCommentDTO {
    //目标帖子ID
    @NotNull(message = "目标帖子ID不能为空")
    private Long postId;
    //被回复的评论ID
    private Long replyToCommentId;
    //被回复的用户ID
    private Long replyToUserId;
    // 评论内容
    @NotBlank(message = "评论内容不能为空")
    @Size(max = 500, message = "评论内容长度不能超过500")
    private String content;
    // 图片链接（先用 URL，后续可改文件上传）
    private String imageUrl;
    //文件ID
    private Long fileId;
}

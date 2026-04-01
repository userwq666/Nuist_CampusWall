package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.CommentStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminCommentDetailVO {
    //评论ID
    private Long commentId;
    //评论人ID
    private Long userId;
    //目标帖子ID
    private Long postId;
    //被回复评论ID
    private Long replyToCommentId;
    //被回复用户ID
    private Long replyToUserId;
    //评论内容
    private String content;
    //图片URL
    private String imageUrl;
    //点赞数量
    private Integer likeCount;
    //评论状态
    private CommentStatus status;
    //创建时间
    private LocalDateTime createTime;
}

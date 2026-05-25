package com.nuist_campuswall.dto.comment;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentVO {
    // 评论ID
    private Long id;
    // 评论作者ID（关联 user.id）
    private Long userId;
    // 评论者用户名
    private String username;
    // 目标帖子ID（关联 post.id）
    private Long postId;
    // 被回复的评论ID，为空表示顶层评论
    private Long replyToCommentId;
    // 被回复的用户ID
    private Long replyToUserId;
    // 被回复的用户名
    private String replyToUsername;
    // 被回复评论的楼层号
    private Integer replyToFloor;
    // 帖子作者用户名（直接评论时@发帖人用）
    private String postAuthorUsername;
    // 评论内容
    private String content;
    // 可选的图片 URL
    private String imageUrl;
    //点赞数量
    private Integer likeCount;
    // 创建时间
    private LocalDateTime createTime;
}
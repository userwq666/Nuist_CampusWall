package com.nuist_campuswall.domain.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    // 评论主键ID
    private Long id;
    // 评论用户ID（关联 User.id）
    private Long userId;
    // 所属帖子ID（关联 Post.id）
    private Long postId;
    // 评论正文
    private String content;
    // 评论图片URL（可选）
    private String imageUrl;
    // 评论状态：启用/禁用
    private CommentStatus status;
    // 创建时间
    private LocalDateTime createTime;
}

package com.nuist_campuswall.domain.comment;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nuist_campuswall.domain.enums.CommentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment")
public class Comment {
    // 主键 ID
    @TableId(type = IdType.AUTO)
    private Long id;
    // 评论作者 ID（关联 user.id）
    private Long userId;
    // 目标帖子 ID（关联 post.id）
    private Long postId;
    // 被回复的评论 ID，为空表示顶层评论
    private Long replyToCommentId;
    // 被回复的用户 ID
    private Long replyToUserId;
    // 评论内容
    private String content;
    // 可选的图片 URL
    private String imageUrl;
    // 评论状态
    private CommentStatus status;
    //点赞数量
    private Integer likeCount;
    // 创建时间
    private LocalDateTime createTime;
}

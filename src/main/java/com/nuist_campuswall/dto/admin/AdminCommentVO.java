package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.CommentStatus;
import lombok.Data;

@Data
public class AdminCommentVO {
    //评论ID
    private Long commentId;
    //目标帖子ID
    private Long postId;
    //评论人ID
    private Long userId;
    //评论状态
    private CommentStatus status;
}

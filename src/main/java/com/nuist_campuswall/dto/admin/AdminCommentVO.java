package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.CommentStatus;
import lombok.Data;

@Data
public class AdminCommentVO {
    //评论ID
    private Long commentId;
    //目标帖子ID
    private Long postId;
    //目标帖子标题
    private String postTitle;
    //评论内容
    private String content;
    //评论人ID
    private Long userId;
    //评论人用户名
    private String username;
    //楼层
    private Integer floor;
    //评论状态
    private CommentStatus status;
}

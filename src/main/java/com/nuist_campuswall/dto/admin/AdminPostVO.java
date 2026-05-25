package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.PostStatus;
import lombok.Data;

@Data
public class AdminPostVO {
    // 帖子ID
    private Long postId;
    // 标题
    private String title;
    // 正文
    private String content;
    // 发帖人ID
    private Long userId;
    // 发帖人用户名
    private String username;
    // 帖子状态
    private PostStatus status;
}

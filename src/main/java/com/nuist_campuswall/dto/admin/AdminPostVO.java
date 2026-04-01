package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.PostStatus;
import lombok.Data;

@Data
public class AdminPostVO {
    // 帖子ID
    private Long postId;
    // 标题
    private String title;
    // 发帖人ID
    private Long userId;
    // 帖子状态
    private PostStatus status;
}

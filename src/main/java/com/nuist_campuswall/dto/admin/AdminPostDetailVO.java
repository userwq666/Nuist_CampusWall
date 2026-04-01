package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.PostStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminPostDetailVO {
    // 帖子ID
    private Long postId;
    // 发帖人ID
    private Long userId;
    // 标题
    private String title;
    // 正文
    private String content;
    // 图片URL
    private String imageUrl;
    // 点赞数
    private Integer likeCount;
    // 状态
    private PostStatus status;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
}
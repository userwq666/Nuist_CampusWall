package com.nuist_campuswall.dto.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// 帖子返回对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostVO {
    // 帖子ID
    private Long id;
    // 作者用户ID
    private Long userId;
    // 标题
    private String title;
    // 内容
    private String content;
    // 图片URL
    private String imageUrl;
    // 点赞数
    private Integer likeCount;
    // 创建时间
    private LocalDateTime createTime;

}


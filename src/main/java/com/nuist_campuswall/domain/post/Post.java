package com.nuist_campuswall.domain.post;

import com.nuist_campuswall.domain.enums.PostStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    // 主键ID
    private Long id;
    // 发帖用户ID（关联 User.id）
    private Long userId;
    // 帖子标题
    private String title;
    // 帖子正文
    private String content;
    // 图片链接（先用 URL，后续可改文件上传）
    private String imageUrl;
    // 帖子状态：启用/下架
    private PostStatus status;
    //点赞次数
    private Integer likeCount;
    // 创建时间（发帖时间）
    private LocalDateTime createTime;
    // 最后更新时间
    private LocalDateTime updateTime;
}

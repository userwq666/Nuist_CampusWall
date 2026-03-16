package com.nuist_campuswall.domain.like;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.nuist_campuswall.domain.enums.TargetType;

import java.time.LocalDateTime;

/**
 * 点赞记录实体
 */
// 点赞记录实体
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Like {
    // 主键 ID
    private Long id;
    // 点赞用户 ID
    private Long userId;
    // 点赞目标类型（帖子/评论）
    private TargetType targetType;
    // 点赞目标 ID（帖子 ID 或评论 ID）
    private Long targetId;
    // 点赞时间
    private LocalDateTime createTime;
}

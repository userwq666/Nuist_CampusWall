package com.nuist_campuswall.domain.like;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nuist_campuswall.domain.enums.TargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 点赞记录实体
 */
// 点赞记录实体
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("`like`")
public class Like {
    // 主键 ID
    @TableId(type = IdType.AUTO)
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

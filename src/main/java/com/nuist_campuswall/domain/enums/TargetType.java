package com.nuist_campuswall.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TargetType {
    // 帖子点赞目标
    POST(1, "post"),
    // 评论点赞目标
    COMMENT(2, "comment");

    // 入库值（对应 like.target_type 字段）
    @EnumValue
    private final Integer code;
    // 文本标识
    private final String label;
}

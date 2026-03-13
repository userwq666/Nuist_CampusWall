package com.nuist_campuswall.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CommentStatus {
    // 可见
    ENABLE(1, "enable"),
    // 不可见
    DISABLE(0, "disable");

    // 入库值（对应 comment.status 字段）
    @EnumValue
    private final Integer code;
    // 文本标识
    private final String label;
}

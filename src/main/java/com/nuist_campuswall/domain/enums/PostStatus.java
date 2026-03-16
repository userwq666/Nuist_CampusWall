package com.nuist_campuswall.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PostStatus {
    // 正常展示
    ENABLE(1, "enable"),
    // 下架/隐藏
    DISABLE(0, "disable");

    // 入库值（对应 post.status 字段）
    @EnumValue
    private final Integer code;
    // 文本标识
    private final String label;
}

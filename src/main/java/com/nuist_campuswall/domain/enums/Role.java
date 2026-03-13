package com.nuist_campuswall.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Role {
    // 管理员
    ADMIN(1, "admin"),
    // 普通用户
    USER(0, "user");

    // 入库值（对应 role 字段）
    @EnumValue
    private final Integer code;
    // 角色文本标识
    private final String label;
}

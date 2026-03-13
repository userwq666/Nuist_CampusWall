package com.nuist_campuswall.domain.enums;

public enum Role {
    // 管理员
    ADMIN("admin", 1),
    // 普通用户
    USER("user", 0);

    // 角色文本标识
    private final String label;
    // 角色数值编码（可用于数据库存储）
    private final int code;

    Role(String label, int code) {
        this.label = label;
        this.code = code;
    }

    public String getLabel() {
        return label;
    }

    public int getCode() {
        return code;
    }
}

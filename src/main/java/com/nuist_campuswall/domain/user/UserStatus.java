package com.nuist_campuswall.domain.user;

public enum UserStatus {
    // 启用
    ENABLE("enable", 1),
    // 禁用
    DISABLE("disable", 0);

    // 状态文本标识
    private final String label;
    // 状态数值编码（可用于数据库存储）
    private final int code;

    UserStatus(String label, int code) {
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

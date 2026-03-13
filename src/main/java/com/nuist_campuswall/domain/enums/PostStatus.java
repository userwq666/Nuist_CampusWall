package com.nuist_campuswall.domain.enums;

public enum PostStatus {
    // 正常展示
    ENABLE("enable", 1),
    // 下架/隐藏
    DISABLE("disable", 0);

    // 状态文本标识
    private final String label;
    // 状态数值编码（可用于数据库存储）
    private final int code;

    PostStatus(String label, int code) {
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

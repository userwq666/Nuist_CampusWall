package com.nuist_campuswall.domain.comment;

public enum CommentStatus {
    // 可见
    ENABLE("enable",1),
    // 不可见
    DISABLE("disable",0);

    // 状态文本标识
    private final String label;
    // 状态数值编码（可用于数据库存储）
    private final int code;

    CommentStatus(String label, int code) {
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

package com.nuist_campuswall.domain.comment;

public enum CommentStatus {
    ENABLE("enable",1),
    DISABLE("disable",0);

    private final String label;
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

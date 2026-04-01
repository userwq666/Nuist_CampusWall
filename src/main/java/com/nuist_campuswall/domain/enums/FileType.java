package com.nuist_campuswall.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileType {
    //头像
    AVATAR(1, "avatar"),
    //帖子
    POST(2, "post"),
    //评论
    COMMENT(3, "comment");

    // 入库值（对应 role 字段）
    @EnumValue
    private final Integer code;
    // 角色文本标识
    private final String label;
}

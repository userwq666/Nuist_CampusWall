package com.nuist_campuswall.domain.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileBizType {
    //空业务
    EMPTY(0, ""),
    //头像
    AVATAR(1, "avatar"),
    //帖子
    POST(2, "post"),
    //评论
    COMMENT(3, "comment");

    @EnumValue
    private final Integer code;
    private final String label;
}

package com.nuist_campuswall.domain.enums;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FileStatus {

    TEMP(0, "temp"),
    BOUND(1, "bound"),
    DELETED(2, "deleted");

    @EnumValue
    private final Integer code;
    private final String label;
}

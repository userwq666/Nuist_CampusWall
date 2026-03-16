package com.nuist_campuswall.common;

import lombok.Getter;

// 自定义业务异常类
@Getter
public class BusinessException extends RuntimeException {
    private final Integer code;    // 状态码

    // 构造方法
    public BusinessException(Integer code, String message){
        super(message);
        this.code = code;
    }

    public BusinessException(String message){
        super(message);
        this.code = 1;
    }
}

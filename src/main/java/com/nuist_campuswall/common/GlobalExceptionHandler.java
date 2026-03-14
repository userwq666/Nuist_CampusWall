package com.nuist_campuswall.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice    // 全局异常处理类
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    // 处理业务异常
    public Result<Void> handleBusinessException(BusinessException e){
        return Result.fail(e.getCode(), e.getMessage());
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e){
        return Result.fail(5000, "服务器异常,请重试");
    }
}

package com.nuist_campuswall.common;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice    // 全局异常处理类
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    // 处理业务异常
    public Result<Void> handleBusinessException(BusinessException e){
        return Result.fail(e.getCode(), e.getMessage());
    }

    //参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)        // MethodArgumentNotValidException 表示参数校验异常
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        String msg = "参数校验失败";
        if (e.getBindingResult().hasFieldErrors()) {        // 判断参数校验结果是否为空
            msg = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();  // 获取参数校验结果中的第一个错误信息
        }
        return Result.fail(ErrorCode.PARAMETER_ERROR, msg);
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e){
        return Result.fail(ErrorCode.SERVER_ERROR, "服务器异常,请重试");
    }
}

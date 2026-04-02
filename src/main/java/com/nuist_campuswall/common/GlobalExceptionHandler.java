package com.nuist_campuswall.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

// 全局异常处理类
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final ErrorCodeToHttpStatus errorCodeToHttpStatus;

    // 处理业务异常
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Result<Void>> handleBusinessException(BusinessException e) {
        return ResponseEntity
                .status(errorCodeToHttpStatus.toHttpStatus(e.getCode()))
                .body(Result.fail(e.getCode(), e.getMessage()));
    }

    // 参数校验异常
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String msg = "参数校验失败";
        if (e.getBindingResult().hasFieldErrors()) {
            msg = e.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        }
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(Result.fail(ErrorCode.PARAMETER_ERROR, msg));
    }

    // 上传大小超限异常
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Result<Void>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(Result.fail(ErrorCode.FILE_SIZE_ERROR, "文件大小超过上传限制"));
    }

    // 处理其他异常
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.fail(ErrorCode.SERVER_ERROR, "服务器异常,请重试"));
    }
}
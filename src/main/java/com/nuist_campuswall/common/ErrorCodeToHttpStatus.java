package com.nuist_campuswall.common;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

// 业务错误码 -> HTTP状态码 映射器
@Component
public class ErrorCodeToHttpStatus {

    // 根据业务错误码返回对应的HTTP状态码
    public HttpStatus toHttpStatus(Integer code) {

        // 参数校验
        if (code == null) {
            return HttpStatus.BAD_REQUEST;
        }

        // 根据业务错误码返回对应的 HTTP 状态码
        switch (code) {
            case ErrorCode.USERNAME_EXISTS:
            case ErrorCode.EMAIL_EXISTS:
            case ErrorCode.LIKE_EXIST:
            case ErrorCode.FILE_STATUS_ERROR:
            case ErrorCode.POST_STATUS_ERROR:
                return HttpStatus.CONFLICT;         // 409 冲突

            case ErrorCode.USERNAME_NOT_FOUND:
            case ErrorCode.USER_NOT_FOUND:
            case ErrorCode.POST_NOT_FOUND:
            case ErrorCode.COMMENT_NOT_FOUND:
            case ErrorCode.FILE_NOT_FOUND:
                return HttpStatus.NOT_FOUND;       // 404 未找到

            case ErrorCode.PASSWORD_INCORRECT:
            case ErrorCode.UNAUTHORIZED:
            case ErrorCode.TOKEN_INVALID:
                return HttpStatus.UNAUTHORIZED;   // 401 未授权

            case ErrorCode.NO_PERMISSION:
                return HttpStatus.FORBIDDEN;     // 403 禁止

            case ErrorCode.PARAMETER_ERROR:
                return HttpStatus.UNPROCESSABLE_ENTITY; // 422 无法处理的实体

            case ErrorCode.FILE_TYPE_ERROR:
                return HttpStatus.UNSUPPORTED_MEDIA_TYPE; // 415 不支持的媒体类型

            case ErrorCode.FILE_SIZE_ERROR:
                return HttpStatus.PAYLOAD_TOO_LARGE; // 413 负载太 large

            case ErrorCode.FILE_UPLOAD_ERROR:
            case ErrorCode.FILE_DELETE_ERROR:
            case ErrorCode.SERVER_ERROR:
                return HttpStatus.INTERNAL_SERVER_ERROR; // 500 内部服务器错误

            default:
                return HttpStatus.BAD_REQUEST;  // 400 错误的请求
        }
    }
}
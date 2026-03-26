package com.nuist_campuswall.common;

// 统一错误码常量
public final class ErrorCode {

    private ErrorCode() {
    }

    // 通用
    public static final int SERVER_ERROR = 500;          // 服务器异常
    public static final int PARAMETER_ERROR = 422;       // 参数错误

    // 账户注册/登录
    public static final int USERNAME_EXISTS = 401;       // 用户名已存在
    public static final int EMAIL_EXISTS = 402;          // 邮箱已存在
    public static final int USERNAME_NOT_FOUND = 403;    // 用户名不存在
    public static final int USER_DISABLED = 404;         // 用户被禁用
    public static final int PASSWORD_INCORRECT = 405;    // 密码错误

    // 鉴权
    public static final int UNAUTHORIZED = 406;          // 未登录或token缺失
    public static final int TOKEN_INVALID = 407;         // token无效
    public static final int USER_NOT_FOUND = 408;        // token对应用户不存在
    public static final int NO_PERMISSION = 412;         // 无权限

    // 业务资源
    public static final int POST_NOT_FOUND = 409;        // 帖子不存在
    public static final int COMMENT_NOT_FOUND = 410;     // 评论不存在
    public static final int LIKE_EXIST = 411;            // 已点赞，不可重复点赞
    public static final int POST_STATUS_ERROR = 413;     // 帖子状态错误
}
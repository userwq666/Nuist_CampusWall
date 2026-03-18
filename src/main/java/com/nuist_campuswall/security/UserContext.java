package com.nuist_campuswall.security;

//当前用户信息
public class UserContext {
    //当前用户ID
    private static final ThreadLocal<Long>   //ThreadLocal类，用于线程安全地存储数据
            USER_ID_HOLDER = new ThreadLocal<>();

    //设置当前用户ID
    public static void setUserId(Long userId) {
        USER_ID_HOLDER.set(userId);
    }

    //获取当前用户ID
    public static Long getUserId() {
        return USER_ID_HOLDER.get();
    }

    //清除当前用户ID
    public static void clear() {
        USER_ID_HOLDER.remove();
    }

}

package com.nuist_campuswall.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 统一返回结果类
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;  // 状态码 0成功 ,!0失败
    private String message;  //提示信息
    private T data;          //业务数据

    //成功
    public static <T> Result<T> success(T data){
        return new Result<>(0, "success", data);
    }

    //失败
    public static <T> Result<T> fail(Integer code, String message){
        return new Result<>(code, message, null);
    }

    public static <T> Result<T> fail(Integer code, String message, T data){
        return new Result<>(code, message, data);
    }

    public static <T> Result<T> fail(String message){     //默认失败，状态码为1
        return new Result<>(1, message, null);
    }
}

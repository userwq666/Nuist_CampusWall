package com.nuist_campuswall.dto.account;


import lombok.Data;

//登录返回值
@Data
public class LoginRespVO {
    //jwt令牌
    private String token;
    //登录用户信息
    private LoginVO userInfo;
}

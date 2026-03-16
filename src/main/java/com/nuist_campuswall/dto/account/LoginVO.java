package com.nuist_campuswall.dto.account;

import lombok.Data;

//登录视图对象
@Data
public class LoginVO {
    //用户ID
    private Long id;
    //用户名
    private String username;
    //昵称
    private String nickname;
    //用户邮箱
    private String educationEmail;
    //用户头像
    private String imageUrl;
    //用户角色
    private String role;
    //用户状态
    private String status;

}

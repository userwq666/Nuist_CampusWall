package com.nuist_campuswall.dto.account;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//登录数据传输对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    //用户名
    private String username;
    //密码
    private String password;
}

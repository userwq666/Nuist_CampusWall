package com.nuist_campuswall.dto.comment.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDTO {
    //用户名
    private String username;
    //密码
    private String password;
    //昵称
    private String nickname;
    //邮箱
    private String educationEmail;
}

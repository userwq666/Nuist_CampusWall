package com.nuist_campuswall.dto.account;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

//修改我的信息请求对象
@Data
public class MyInfoDTO {
    //用户昵称（可选）
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    //用户头像（可选）
    @Size(max = 255, message = "头像URL长度不能超过255")
    private String imageUrl;

    //教育邮箱（可选）
    @Email(message = "教育邮箱格式错误")
    @Size(max = 100, message = "教育邮箱长度不能超过100")
    private String educationEmail;

    //旧密码（修改密码时必填）
    @Size(min = 6, max = 32, message = "密码长度应在6到32之间")
    private String oldPassword;

    //新密码（修改密码时必填）
    @Size(min = 6, max = 32, message = "密码长度应在6到32之间")
    private String newPassword;

    //头像文件ID
    private Long fileID;
}


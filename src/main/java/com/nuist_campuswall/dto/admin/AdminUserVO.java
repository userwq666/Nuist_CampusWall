package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.Role;
import com.nuist_campuswall.domain.enums.UserStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminUserVO {
    //用户ID
    private Long userId;
    //用户名
    private String username;
    //用户昵称
    private String nickname;
    //用户邮箱
    private String educationEmail;
    //用户头像
    private String imageUrl;
    //用户角色
    private Role role;
    //用户状态
    private UserStatus status;
    //用户创建时间
    private LocalDateTime createTime;

}

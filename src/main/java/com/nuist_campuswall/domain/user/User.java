package com.nuist_campuswall.domain.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    // 主键ID
    private Long id;
    // 登录账号（唯一）
    private String username;
    // 登录密码（后续应存加密值）
    private String password;
    // 用户昵称
    private String nickname;
    // 头像URL（可选）
    private String imageUrl;
    //学生邮箱
    private String educationEmail;
    // 角色：管理员/普通用户
    private Role role;
    // 账号状态：启用/禁用
    private UserStatus status;
    // 创建时间
    private LocalDateTime createTime;
    // 最后更新时间
    private LocalDateTime updateTime;
}

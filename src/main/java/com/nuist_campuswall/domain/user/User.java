package com.nuist_campuswall.domain.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.nuist_campuswall.domain.enums.Role;
import com.nuist_campuswall.domain.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")        // 标识该类对应数据库中的 user 表
public class User {
    @TableId(type = IdType.AUTO)       // 标识该字段是数据库表的主键，使用自增策略
    private Long id;
    // 登录用户名
    private String username;
    // 加密后的密码
    private String password;
    // 用户昵称
    private String nickname;
    // 头像 URL
    private String imageUrl;
    // 教育邮箱
    private String educationEmail;
    // 用户角色（普通用户/管理员）
    private Role role;
    // 用户状态（启用/禁用）
    private UserStatus status;
    // 创建时间
    private LocalDateTime createTime;
    // 更新时间
    private LocalDateTime updateTime;
}

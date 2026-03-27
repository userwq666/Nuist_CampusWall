package com.nuist_campuswall.service.admin.auth.impl;

import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.enums.Role;
import com.nuist_campuswall.domain.user.User;
import com.nuist_campuswall.mapper.user.UserMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.admin.auth.AdminAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    // 用户表 mapper
    private final UserMapper userMapper;

    // -------------------------管理员权限检查实现---------------------------
    @Override
    public void adminCheck() {
        // 1. 检查是否登录
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或token缺失");
        }

        // 2. 根据 userId 查询用户
        User loginUser = userMapper.selectById(userId);
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        // 3. 角色不是管理员，抛无权限异常
        if (loginUser.getRole() != Role.ADMIN) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "无权限");
        }
    }
}

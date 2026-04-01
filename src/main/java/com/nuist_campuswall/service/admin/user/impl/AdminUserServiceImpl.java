package com.nuist_campuswall.service.admin.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.enums.UserStatus;
import com.nuist_campuswall.domain.user.User;
import com.nuist_campuswall.dto.admin.AdminUserPageDTO;
import com.nuist_campuswall.dto.admin.AdminUserVO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.mapper.user.UserMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.admin.auth.AdminAuthService;
import com.nuist_campuswall.service.admin.user.AdminUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserServiceImpl implements AdminUserService {

    // 管理员权限服务
    private final AdminAuthService adminAuthService;
    // 用户表 mapper
    private final UserMapper userMapper;

    // --------------------------管理员用户分页接口实现--------------------------------
    @Override
    public PageResult<AdminUserVO> pageUser(AdminUserPageDTO dto) {
        // 1. 管理员权限检查
        adminAuthService.adminCheck();

        // 2. 分页查询
        Page<User> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<User> result = userMapper.selectPage(page,
                Wrappers.<User>lambdaQuery()
                        .eq(dto.getStatus() != null, User::getStatus, dto.getStatus())
                        .orderByAsc(User::getId));

        // 3. 转换返回
        return new PageResult<>(
                result.getTotal(),
                result.getRecords().stream().map(this::toAdminVO).toList()
        );
    }

    // ----------------------启用用户接口实现---------------------
    @Override
    public void enableUser(Long userId) {
        //1. 管理员权限检查
        adminAuthService.adminCheck();

        //2.检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        //3.安全校验(管理员不可对自己操作)
        Long loginUserId = UserContext.getUserId();
        if (loginUserId !=null && loginUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "管理员不可对自己操作");
        }
        if (user.getStatus() == UserStatus.ENABLE) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "用户已启用");
        }

        //4.更新用户状态
        User updateUser = new User();                   // 创建更新用户对象
        updateUser.setId(userId);                      // 设置用户ID
        updateUser.setStatus(UserStatus.ENABLE);       // 设置用户状态为启用
        userMapper.updateById(updateUser);             // 更新用户状态
    }

    // ----------------------禁用用户接口实现---------------------
    @Override
    public void disableUser(Long userId) {
        //1. 管理员权限检查
        adminAuthService.adminCheck();

        //2.检查用户是否存在
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        //3.安全校验(管理员不可对自己操作)
        Long loginUserId = UserContext.getUserId();
        if (loginUserId !=null && loginUserId.equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "管理员不可对自己操作");
        }
        if (user.getStatus() == UserStatus.DISABLE) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "用户已禁用");
        }

        //4.更新用户状态
        User updateUser = new User();
        updateUser.setId(userId);
        updateUser.setStatus(UserStatus.DISABLE);
        userMapper.updateById(updateUser);
    }

    // ----------------------User 转 AdminUserVO方法---------------------
    private AdminUserVO toAdminVO(User user) {
        AdminUserVO vo = new AdminUserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEducationEmail(user.getEducationEmail());
        vo.setImageUrl(user.getImageUrl());
        vo.setRole(user.getRole());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }
}

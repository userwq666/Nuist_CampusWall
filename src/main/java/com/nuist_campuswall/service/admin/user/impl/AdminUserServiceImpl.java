package com.nuist_campuswall.service.admin.user.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuist_campuswall.domain.user.User;
import com.nuist_campuswall.dto.admin.AdminUserPageDTO;
import com.nuist_campuswall.dto.admin.AdminUserVO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.mapper.user.UserMapper;
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

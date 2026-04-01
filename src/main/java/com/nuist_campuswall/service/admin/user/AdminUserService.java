package com.nuist_campuswall.service.admin.user;

import com.nuist_campuswall.dto.admin.AdminUserPageDTO;
import com.nuist_campuswall.dto.admin.AdminUserVO;
import com.nuist_campuswall.dto.common.PageResult;

public interface AdminUserService {
    // 管理员用户分页接口
    PageResult<AdminUserVO> pageUser(AdminUserPageDTO dto);

    //启用用户接口
    void enableUser(Long userId);

    //禁用用户接口
    void disableUser(Long userId);
}

package com.nuist_campuswall.controller.admin;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.dto.admin.AdminUserPageDTO;
import com.nuist_campuswall.dto.admin.AdminUserVO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.service.admin.auth.AdminAuthService;
import com.nuist_campuswall.service.admin.user.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    // 管理员权限服务
    private final AdminAuthService adminAuthService;
    // 管理员用户服务
    private final AdminUserService adminUserService;

    // 管理员鉴权测试接口
    @GetMapping("/ping")
    public Result<String> ping() {
        adminAuthService.adminCheck();
        return Result.success("admin ok");
    }

    // 管理员用户分页接口
    @GetMapping("/user/page")
    public Result<PageResult<AdminUserVO>> pageUser(@Valid @ModelAttribute AdminUserPageDTO dto) {
        return Result.success(adminUserService.pageUser(dto));
    }
}

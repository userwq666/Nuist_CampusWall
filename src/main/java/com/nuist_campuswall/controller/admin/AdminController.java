package com.nuist_campuswall.controller.admin;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.dto.admin.*;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.service.admin.auth.AdminAuthService;
import com.nuist_campuswall.service.admin.comment.AdminCommentService;
import com.nuist_campuswall.service.admin.post.AdminPostService;
import com.nuist_campuswall.service.admin.user.AdminUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    // 管理员权限服务
    private final AdminAuthService adminAuthService;
    // 管理员用户服务
    private final AdminUserService adminUserService;
    // 管理员帖子服务
    private final AdminPostService adminPostService;
    //管理员评论服务
    private final AdminCommentService adminCommentService;

    // 管理员鉴权测试接口
    @GetMapping("/ping")
    public Result<String> ping() {
        adminAuthService.adminCheck();
        return Result.success("管理员身份验证成功");
    }

    // 管理员用户分页接口
    @GetMapping("/user/page")
    public Result<PageResult<AdminUserVO>> pageUser(@Valid @ModelAttribute AdminUserPageDTO dto) {
        return Result.success(adminUserService.pageUser(dto));
    }

    // 管理员启用用户接口
    @PostMapping("/user/enable/{userId}")
    public Result<String> enableUser(@PathVariable Long userId) {
        adminUserService.enableUser(userId);
        return Result.success("用户已启用");
    }

    // 管理员禁用用户接口
    @PostMapping("/user/disable/{userId}")
    public Result<String> disableUser(@PathVariable Long userId) {
        adminUserService.disableUser(userId);
        return Result.success("用户已禁用");
    }

    // 管理员帖子分页接口
    @GetMapping("/post/page")
    public Result<PageResult<AdminPostVO>> pagePost(@Valid @ModelAttribute AdminPostPageDTO dto) {
        return Result.success(adminPostService.page(dto));
    }

    // 管理员帖子详情接口
    @GetMapping("/post/detail/{postId}")
    public Result<AdminPostDetailVO> postDetail(@PathVariable Long postId) {
        return Result.success(adminPostService.detail(postId));
    }

    // 管理员帖子启用接口
    @PostMapping("/post/enable/{postId}")
    public Result<String> enablePost(@PathVariable Long postId) {
        adminPostService.enablePost(postId);
        return Result.success("帖子已启用");
    }

    // 管理员帖子禁用接口
    @PostMapping("/post/disable/{postId}")
    public Result<String> disablePost(@PathVariable Long postId) {
        adminPostService.disablePost(postId);
        return Result.success("帖子已禁用");
    }

    // 管理员评论分页接口
    @GetMapping("/comment/page")
    public Result<PageResult<AdminCommentVO>> pageComment(@Valid @ModelAttribute AdminCommentPageDTO dto) {
        return Result.success(adminCommentService.pageComment(dto));
    }

    // 管理员评论详情接口
    @GetMapping("/comment/detail/{commentId}")
    public Result<AdminCommentDetailVO> commentDetail(@PathVariable Long commentId) {
        return Result.success(adminCommentService.detail(commentId));
    }

    // 管理员评论启用接口
    @PostMapping("/comment/enable/{commentId}")
    public Result<String> enableComment(@PathVariable Long commentId) {
        adminCommentService.enableComment(commentId);
        return Result.success("评论已启用");
    }

    // 管理员评论禁用接口
    @PostMapping("/comment/disable/{commentId}")
    public Result<String> disableComment(@PathVariable Long commentId) {
        adminCommentService.disableComment(commentId);
        return Result.success("评论已禁用");
    }
}

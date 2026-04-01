package com.nuist_campuswall.service.admin.post.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.enums.PostStatus;
import com.nuist_campuswall.domain.post.Post;
import com.nuist_campuswall.dto.admin.AdminPostDetailVO;
import com.nuist_campuswall.dto.admin.AdminPostPageDTO;
import com.nuist_campuswall.dto.admin.AdminPostVO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.service.admin.auth.AdminAuthService;
import com.nuist_campuswall.service.admin.post.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminPostServiceImpl implements AdminPostService {

    private final PostMapper postMapper;
    private final AdminAuthService adminAuthService;

    //--------------------帖子分页接口实现---------------------
    @Override
    public PageResult<AdminPostVO> page(AdminPostPageDTO dto) {
        //1.管理员权限验证
        adminAuthService.adminCheck();

        //2.分页查询
        Page<Post> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Post> result = postMapper.selectPage(page,
                Wrappers.<Post>lambdaQuery()
                        .eq(dto.getUserId() != null, Post::getUserId, dto.getUserId())  // 用户ID
                        .eq(dto.getStatus() != null, Post::getStatus, dto.getStatus())  // 状态
                        .orderByDesc(Post::getCreateTime));                                      // 创建时间降序

        //3.返回结果
        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toAdminPostVO).toList());
    }

    //--------------------帖子详情接口实现---------------------
    @Override
    public AdminPostDetailVO detail(Long id) {
        //1.管理员权限验证
        adminAuthService.adminCheck();

        //2.帖子校验
        Post post = postMapper.selectById(id);
        if(post == null){
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }

        //3.返回结果
        return toAdminPostDetailVO(post);
    }

    //--------------------帖子启用接口实现---------------------
    @Override
    public void enablePost(Long id) {
        //1.管理员权限验证
        adminAuthService.adminCheck();

        //2.帖子校验
        Post post = postMapper.selectById(id);
        if(post == null){
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }
        if(post.getStatus() == PostStatus.ENABLE) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "帖子已启用");
        }

        //3.更新帖子状态
        Post updatePost = new Post();
        updatePost.setId(id);
        updatePost.setStatus(PostStatus.ENABLE);
        postMapper.updateById(updatePost);
    }

    //--------------------帖子禁用接口实现---------------------
    @Override
    public void disablePost(Long id) {
        //1.管理员权限验证
        adminAuthService.adminCheck();

        //2.帖子校验
        Post post = postMapper.selectById(id);
        if(post == null){
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }
        if(post.getStatus() == PostStatus.DISABLE) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "帖子已禁用");
        }

        //3.更新帖子状态
        Post updatePost = new Post();
        updatePost.setId(id);
        updatePost.setStatus(PostStatus.DISABLE);
        postMapper.updateById(updatePost);
    }

    //--------------------私有转换方法(post to adminPostVO)---------------------
    private AdminPostVO toAdminPostVO(Post post) {
        AdminPostVO vo = new AdminPostVO();
        vo.setPostId(post.getId());
        vo.setTitle(post.getTitle());
        vo.setUserId(post.getUserId());
        vo.setStatus(post.getStatus());
        return vo;
    }

    //--------------------私有转换方法(post to adminPostDetailVO)---------------------
    private AdminPostDetailVO toAdminPostDetailVO(Post post) {
        AdminPostDetailVO vo = new AdminPostDetailVO();
        vo.setPostId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setImageUrl(post.getImageUrl());
        vo.setLikeCount(post.getLikeCount());
        vo.setStatus(post.getStatus());
        vo.setCreateTime(post.getCreateTime());
        vo.setUpdateTime(post.getUpdateTime());
        return vo;
    }
}

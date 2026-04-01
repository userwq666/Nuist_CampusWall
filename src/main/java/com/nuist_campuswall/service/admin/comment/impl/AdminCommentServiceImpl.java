package com.nuist_campuswall.service.admin.comment.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.comment.Comment;
import com.nuist_campuswall.domain.enums.CommentStatus;
import com.nuist_campuswall.dto.admin.AdminCommentDetailVO;
import com.nuist_campuswall.dto.admin.AdminCommentPageDTO;
import com.nuist_campuswall.dto.admin.AdminCommentVO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.mapper.comment.CommentMapper;
import com.nuist_campuswall.service.admin.auth.AdminAuthService;
import com.nuist_campuswall.service.admin.comment.AdminCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminCommentServiceImpl implements AdminCommentService {

    private final CommentMapper commentMapper;
    private final AdminAuthService adminAuthService;

    //--------------------评论分页接口实现---------------------
    @Override
    public PageResult<AdminCommentVO> pageComment(AdminCommentPageDTO dto) {
        //1. 管理员检查权限
        adminAuthService.adminCheck();

        //2. 分页查询
        Page<Comment> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Comment> commentPage = commentMapper.selectPage(page,
                Wrappers.<Comment>lambdaQuery()
                        .eq(dto.getPostId()!= null, Comment::getPostId, dto.getPostId())
                        .eq(dto.getUserId()!= null, Comment::getUserId, dto.getUserId())
                        .eq(dto.getStatus()!= null, Comment::getStatus, dto.getStatus())
                        .orderByDesc(Comment::getCreateTime)
        );
        
        //3.返回结果
        return new PageResult<>(commentPage.getTotal(), commentPage.getRecords().stream().map(this::toAdminCommentVO).toList());
    }

    //--------------------评论详情接口实现---------------------
    @Override
    public AdminCommentDetailVO detail(Long id) {
        //1. 管理员检查权限
        adminAuthService.adminCheck();

        //2.评论校验
        Comment comment = commentMapper.selectById(id);
        if(comment == null){
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
        }

        //3.返回结果
        return toAdminCommentDetailVO(comment);
    }

    //--------------------评论启用接口实现---------------------
    @Override
    public void enableComment(Long id) {
        //1. 管理员检查权限
        adminAuthService.adminCheck();

        //2. 评论校验
        Comment comment = commentMapper.selectById(id);
        if(comment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
        }
        if(comment.getStatus() == CommentStatus.ENABLE) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "评论已启用");
        }

        //3.更新评论状态
        Comment updateComment = new Comment();
        updateComment.setId(id);
        updateComment.setStatus(CommentStatus.ENABLE);
        commentMapper.updateById(updateComment);
    }

    //--------------------评论禁用接口实现---------------------
    @Override
    public void disableComment(Long id) {
        //1. 管理员检查权限
        adminAuthService.adminCheck();

        //2. 评论校验
        Comment comment = commentMapper.selectById(id);
        if(comment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
        }
        if(comment.getStatus() == CommentStatus.DISABLE) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "评论已禁用");
        }

        //3.更新评论状态
        Comment updateComment = new Comment();
        updateComment.setId(id);
        updateComment.setStatus(CommentStatus.DISABLE);
        commentMapper.updateById(updateComment);
    }

    //--------------------私有转换方法(comment to adminCommentVO)---------------------
    private AdminCommentVO toAdminCommentVO(Comment comment) {
        AdminCommentVO vo = new AdminCommentVO();
        vo.setCommentId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setStatus(comment.getStatus());
        return vo;
    }

    //--------------------私有转换方法(comment to adminCommentDetailVO)---------------------
    private AdminCommentDetailVO toAdminCommentDetailVO(Comment comment) {
        AdminCommentDetailVO vo = new AdminCommentDetailVO();
        vo.setCommentId(comment.getId());
        vo.setPostId(comment.getPostId());
        vo.setUserId(comment.getUserId());
        vo.setReplyToCommentId(comment.getReplyToCommentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        vo.setContent(comment.getContent());
        vo.setImageUrl(comment.getImageUrl());
        vo.setLikeCount(comment.getLikeCount());
        vo.setStatus(comment.getStatus());
        vo.setCreateTime(comment.getCreateTime());
        return vo;
    }
}

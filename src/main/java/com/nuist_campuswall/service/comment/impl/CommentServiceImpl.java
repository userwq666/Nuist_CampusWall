package com.nuist_campuswall.service.comment.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.comment.Comment;
import com.nuist_campuswall.domain.enums.CommentStatus;
import com.nuist_campuswall.domain.post.Post;
import com.nuist_campuswall.dto.comment.CommentVO;
import com.nuist_campuswall.dto.comment.CreateCommentDTO;
import com.nuist_campuswall.dto.comment.PageCommentDTO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.mapper.comment.CommentMapper;
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;

    //---------------创建评论接口实现------------------
    @Override
    public void create(CreateCommentDTO dto) {
        //1.读取当前用户
        Long userId = UserContext.getUserId();
        if (userId == null) {
           throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        //2.读取当前帖子
        Post post = postMapper.selectById(dto.getPostId());
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "目标帖子不存在");
        }

        //3.组装评论实体
        Comment comment = new Comment();
        comment.setUserId(userId);
        comment.setPostId(dto.getPostId());
        comment.setReplyToCommentId(dto.getReplyToCommentId());
        comment.setReplyToUserId(dto.getReplyToUserId());
        comment.setContent(dto.getContent());
        comment.setImageUrl(dto.getImageUrl());
        comment.setStatus(CommentStatus.ENABLE);
        comment.setLikeCount(0);
        comment.setCreateTime(java.time.LocalDateTime.now());

        //4.插入评论
        commentMapper.insert(comment);
    }

    //---------------查询评论接口实现------------------
    @Override
    public PageResult<CommentVO> page(PageCommentDTO dto) {
        //创建 MyBatis-Plus 的分页对象
        Page<Comment> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        //执行分页查询
        Page<Comment> result = commentMapper.selectPage(
                page,
                Wrappers.<Comment>lambdaQuery()
                        .eq(Comment::getPostId, dto.getPostId())
                        .orderByDesc(Comment::getCreateTime)
        );

        //将 MyBatis-Plus 的 Page 对象转换为 PageResult 对象
        List<CommentVO> records = result.getRecords().stream().map(this::toCommentVO).toList();
        return new PageResult<>(result.getTotal(), records);
    }

    //---------------私有工具方法------------------
    private CommentVO toCommentVO(Comment comment) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setPostId(comment.getPostId());
        vo.setContent(comment.getContent());
        vo.setImageUrl(comment.getImageUrl());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreateTime(comment.getCreateTime());
        vo.setReplyToCommentId(comment.getReplyToCommentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        return vo;
    }
}

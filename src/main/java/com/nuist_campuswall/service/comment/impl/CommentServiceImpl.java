package com.nuist_campuswall.service.comment.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.comment.Comment;
import com.nuist_campuswall.domain.enums.CommentStatus;
import com.nuist_campuswall.domain.enums.FileType;
import com.nuist_campuswall.domain.file.FileAsset;
import com.nuist_campuswall.domain.post.Post;
import com.nuist_campuswall.dto.comment.CommentVO;
import com.nuist_campuswall.dto.comment.CreateCommentDTO;
import com.nuist_campuswall.dto.comment.MyPageCommentDTO;
import com.nuist_campuswall.dto.comment.PageCommentDTO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.mapper.comment.CommentMapper;
import com.nuist_campuswall.mapper.file.FileAssetMapper;
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.comment.CommentService;
import com.nuist_campuswall.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final FileService fileService;
    private final FileAssetMapper fileAssetMapper;

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
        comment.setImageUrl(null);
        comment.setStatus(CommentStatus.ENABLE);
        comment.setLikeCount(0);
        comment.setCreateTime(java.time.LocalDateTime.now());

        //4.插入评论
        commentMapper.insert(comment);

        //5.文件绑定
        if(dto.getFileId()!=null){
            //5.1绑定文件到评论
            fileService.bindFileToBiz(dto.getFileId(), FileType.COMMENT,comment.getId());

            //5.2读取url并填回
            FileAsset fileAsset = fileAssetMapper.selectById(dto.getFileId());
            if(fileAsset!=null){
                Comment updataComment =new Comment();
                updataComment.setId(comment.getId());
                updataComment.setImageUrl(fileAsset.getUrl());
                commentMapper.updateById(updataComment);
            }
        }
    }

    //---------------查询评论接口实现(公开)------------------
    @Override
    public PageResult<CommentVO> page(PageCommentDTO dto) {
        //创建 MyBatis-Plus 的分页对象
        Page<Comment> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        //执行分页查询
        Page<Comment> result = commentMapper.selectPage(
                page,
                Wrappers.<Comment>lambdaQuery()
                        .eq(Comment::getPostId, dto.getPostId())
                        .eq(Comment::getStatus, CommentStatus.ENABLE)
                        .orderByDesc(Comment::getCreateTime)
        );

        //将 MyBatis-Plus 的 Page 对象转换为 PageResult 对象且返回
        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toCommentVO).toList());
    }

    //---------------查询评论接口实现(私有)------------------
    @Override
    public PageResult<CommentVO> myPage(MyPageCommentDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        Page<Comment> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Comment> result = commentMapper.selectPage(
                page,
                Wrappers.<Comment>lambdaQuery()
                        .eq(Comment::getUserId, userId)
                        .eq(Comment::getStatus, CommentStatus.ENABLE)
                        .orderByDesc(Comment::getCreateTime)
        );

        List<CommentVO> records = result.getRecords().stream().map(this::toCommentVO).toList();
        return new PageResult<>(result.getTotal(), records);
    }


    //---------------删除评论接口实现------------------
    @Override
    public void deleteMyComment(Long id) {
        // 1) 登录校验
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        // 2) 评论存在校验
        Comment dbComment = commentMapper.selectById(id);
        if (dbComment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
        }

        // 3) 权限校验：只能删自己的评论
        if (!userId.equals(dbComment.getUserId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "无权删除他人评论");
        }

        // 4) 软删除
        Comment updateComment = new Comment();
        updateComment.setId(id);
        updateComment.setStatus(CommentStatus.DISABLE);
        commentMapper.updateById(updateComment);

        // 5) 删除文件绑定
        fileService.markTempByBiz(FileType.COMMENT, updateComment.getId());
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

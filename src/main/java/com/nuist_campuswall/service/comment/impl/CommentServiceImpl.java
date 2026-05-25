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
import com.nuist_campuswall.domain.user.User;
import com.nuist_campuswall.dto.comment.CommentVO;
import com.nuist_campuswall.dto.comment.CreateCommentDTO;
import com.nuist_campuswall.dto.comment.MyPageCommentDTO;
import com.nuist_campuswall.dto.comment.PageCommentDTO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.mapper.comment.CommentMapper;
import com.nuist_campuswall.mapper.file.FileAssetMapper;
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.mapper.user.UserMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.comment.CommentService;
import com.nuist_campuswall.service.file.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;
    private final FileService fileService;
    private final FileAssetMapper fileAssetMapper;

    //---------------创建评论接口实现------------------
    @Override
    public void create(CreateCommentDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
           throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        Post post = postMapper.selectById(dto.getPostId());
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "目标帖子不存在");
        }

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

        commentMapper.insert(comment);

        if(dto.getFileId()!=null){
            fileService.bindFileToBiz(dto.getFileId(), FileType.COMMENT,comment.getId());

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
        // 1. 先查帖子，获取帖子作者
        Post post = postMapper.selectById(dto.getPostId());
        String postAuthorUsername = null;
        if (post != null) {
            User postAuthor = userMapper.selectById(post.getUserId());
            postAuthorUsername = postAuthor != null ? postAuthor.getUsername() : null;
        }

        // 2. 查询当前帖子所有已启用评论的ID（用于计算楼层号）
        List<Comment> allComments = commentMapper.selectList(
                Wrappers.<Comment>lambdaQuery()
                        .select(Comment::getId, Comment::getCreateTime)
                        .eq(Comment::getPostId, dto.getPostId())
                        .eq(Comment::getStatus, CommentStatus.ENABLE)
                        .orderByAsc(Comment::getCreateTime)
        );
        Map<Long, Integer> floorMap = new HashMap<>();
        for (int i = 0; i < allComments.size(); i++) {
            floorMap.put(allComments.get(i).getId(), i + 1);
        }

        // 3. 分页查询评论
        Page<Comment> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Comment> result = commentMapper.selectPage(
                page,
                Wrappers.<Comment>lambdaQuery()
                        .eq(Comment::getPostId, dto.getPostId())
                        .eq(Comment::getStatus, CommentStatus.ENABLE)
                        .orderByAsc(Comment::getCreateTime)
        );

        List<Comment> comments = result.getRecords();

        // 4. 批量查询所有相关用户的用户名
        Set<Long> userIds = new HashSet<>();
        for (Comment c : comments) {
            userIds.add(c.getUserId());
            if (c.getReplyToUserId() != null) {
                userIds.add(c.getReplyToUserId());
            }
        }
        Map<Long, String> usernameMap = Collections.emptyMap();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            usernameMap = users.stream().collect(Collectors.toMap(User::getId, User::getUsername));
        }

        // 5. 转换为 CommentVO
        final String finalPostAuthorUsername = postAuthorUsername;
        final Map<Long, String> finalUsernameMap = usernameMap;
        List<CommentVO> records = comments.stream()
                .map(c -> toCommentVO(c, finalPostAuthorUsername, finalUsernameMap, floorMap))
                .toList();

        return new PageResult<>(result.getTotal(), records);
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

        Set<Long> userIds = new HashSet<>();
        for (Comment c : result.getRecords()) {
            userIds.add(c.getUserId());
            if (c.getReplyToUserId() != null) {
                userIds.add(c.getReplyToUserId());
            }
        }
        Map<Long, String> usernameMap;
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            usernameMap = users.stream().collect(Collectors.toMap(User::getId, User::getUsername));
        } else {
            usernameMap = Collections.emptyMap();
        }

        // 我的评论列表不关心楼层号和帖子作者
        List<CommentVO> records = result.getRecords().stream()
                .map(c -> toCommentVO(c, null, usernameMap, Collections.emptyMap()))
                .toList();
        return new PageResult<>(result.getTotal(), records);
    }


    //---------------删除评论接口实现------------------
    @Override
    public void deleteMyComment(Long id) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        Comment dbComment = commentMapper.selectById(id);
        if (dbComment == null) {
            throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
        }

        if (!userId.equals(dbComment.getUserId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "无权删除他人评论");
        }

        Comment updateComment = new Comment();
        updateComment.setId(id);
        updateComment.setStatus(CommentStatus.DISABLE);
        commentMapper.updateById(updateComment);

        fileService.markTempByBiz(FileType.COMMENT, updateComment.getId());
    }


    //---------------私有工具方法------------------
    private CommentVO toCommentVO(Comment comment, String postAuthorUsername,
                                   Map<Long, String> usernameMap, Map<Long, Integer> floorMap) {
        CommentVO vo = new CommentVO();
        vo.setId(comment.getId());
        vo.setUserId(comment.getUserId());
        vo.setUsername(usernameMap.getOrDefault(comment.getUserId(), String.valueOf(comment.getUserId())));
        vo.setPostId(comment.getPostId());
        vo.setContent(comment.getContent());
        vo.setImageUrl(comment.getImageUrl());
        vo.setLikeCount(comment.getLikeCount());
        vo.setCreateTime(comment.getCreateTime());
        vo.setReplyToCommentId(comment.getReplyToCommentId());
        vo.setReplyToUserId(comment.getReplyToUserId());
        if (comment.getReplyToUserId() != null) {
            vo.setReplyToUsername(usernameMap.getOrDefault(comment.getReplyToUserId(), String.valueOf(comment.getReplyToUserId())));
        }
        if (comment.getReplyToCommentId() != null) {
            vo.setReplyToFloor(floorMap.get(comment.getReplyToCommentId()));
        }
        vo.setPostAuthorUsername(postAuthorUsername);
        return vo;
    }
}
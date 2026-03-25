package com.nuist_campuswall.service.like.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.comment.Comment;
import com.nuist_campuswall.domain.enums.TargetType;
import com.nuist_campuswall.domain.like.Like;
import com.nuist_campuswall.domain.post.Post;
import com.nuist_campuswall.dto.like.LikeDTO;
import com.nuist_campuswall.dto.like.UnlikeDTO;
import com.nuist_campuswall.mapper.comment.CommentMapper;
import com.nuist_campuswall.mapper.like.LikeMapper;
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final PostMapper postMapper;
    private final CommentMapper commentMapper;
    private final LikeMapper likeMapper;

    //--------------点赞接口实现------------------
    @Override
    public void like(LikeDTO dto) {
        //1. 判断用户登录
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        //2.判断目标是否存在
        TargetType targetType = dto.getTargetType();
        switch (targetType) {
            case POST:
                Post post = postMapper.selectById(dto.getTargetId());
                if (post == null) {
                    throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
                }
                break;
            case COMMENT:
                Comment comment = commentMapper.selectById(dto.getTargetId());
                if (comment == null) {
                    throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
                }
                break;
            default:
                    throw new BusinessException(ErrorCode.PARAMETER_ERROR, "目标类型错误");
        }

        //3.判断用户是否已经点赞
        Long count = likeMapper.selectCount(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetId, dto.getTargetId())
                        .eq(Like::getTargetType, dto.getTargetType())
        );
        if (count>0){
            throw new BusinessException(ErrorCode.LIKE_EXIST, "无法重复点赞");
        }

        //4.插入点赞记录
        Like like = new Like();
        like.setUserId(UserContext.getUserId());
        like.setTargetId(dto.getTargetId());
        like.setTargetType(dto.getTargetType());
        like.setCreateTime(LocalDateTime.now());
        likeMapper.insert(like);

        //5.更新点赞数量
        switch (targetType) {
            case POST:
                Post dbPost = postMapper.selectById(dto.getTargetId());
                if(dbPost != null){
                    Integer PostCount = dbPost.getLikeCount()==null?0:dbPost.getLikeCount();      //帖子点赞数量如果为null，则初始化为0
                    Post updatePost = new Post();
                    updatePost.setId(dto.getTargetId());
                    updatePost.setLikeCount(PostCount+1);
                    postMapper.updateById(updatePost);                      //更新点赞数量
                }else {
                    throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
                }
                break;
            case COMMENT:
                Comment dbComment = commentMapper.selectById(dto.getTargetId());
                if(dbComment != null){
                    Integer CommentCount = dbComment.getLikeCount()==null?0:dbComment.getLikeCount();    //评论点赞数量如果为null，则初始化为0
                    Comment updateComment = new Comment();
                    updateComment.setId(dto.getTargetId());
                    updateComment.setLikeCount(CommentCount+1);
                    commentMapper.updateById(updateComment);                      //更新点赞数量
                }else {
                    throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
                }
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "目标类型错误");
        }
    }

    //--------------取消点赞接口实现------------------
    @Override
    public void unlike(UnlikeDTO dto) {
        //1. 获取用户ID
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        //2.查询点赞记录
        Like dblike = likeMapper.selectOne(
                new LambdaQueryWrapper<Like>()
                        .eq(Like::getUserId, userId)
                        .eq(Like::getTargetId, dto.getTargetId())
                        .eq(Like::getTargetType, dto.getTargetType())
        );
        if (dblike == null) {
            throw new BusinessException(ErrorCode.PARAMETER_ERROR, "用户没有点赞");
        }

        //3.删除点赞记录
        likeMapper.deleteById(dblike.getId());

        //4.更新点赞数量
        switch (dto.getTargetType()) {
            case POST:
                Post dbPost = postMapper.selectById(dto.getTargetId());
                if(dbPost != null){
                    Integer PostCount = dbPost.getLikeCount()==null?0:dbPost.getLikeCount();
                    Post updatePost = new Post();
                    updatePost.setId(dto.getTargetId());
                    updatePost.setLikeCount(Math.max(PostCount-1, 0));      //减1，不能小于0
                    postMapper.updateById(updatePost);
                }else {
                    throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
                }
                break;
            case COMMENT:
                Comment dbComment = commentMapper.selectById(dto.getTargetId());
                if(dbComment != null){
                    Integer CommentCount = dbComment.getLikeCount()==null?0:dbComment.getLikeCount();
                    Comment updateComment = new Comment();
                    updateComment.setId(dto.getTargetId());
                    updateComment.setLikeCount(Math.max(CommentCount-1, 0));
                    commentMapper.updateById(updateComment);
                }else {
                    throw new BusinessException(ErrorCode.COMMENT_NOT_FOUND, "评论不存在");
                }
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMETER_ERROR, "目标类型错误");
        }
    }
}

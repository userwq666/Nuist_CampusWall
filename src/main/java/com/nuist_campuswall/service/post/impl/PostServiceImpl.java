package com.nuist_campuswall.service.post.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.enums.PostStatus;
import com.nuist_campuswall.domain.post.Post;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.dto.post.CreatePostDTO;
import com.nuist_campuswall.dto.post.PagePostDTO;
import com.nuist_campuswall.dto.post.PostVO;
import com.nuist_campuswall.dto.post.UpdatePostDTO;
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    // 公告管理员ID（配置项）
    @Value("${app.admin-user-id:1}")
    private Long adminUserId;

    //--------------------发帖接口实现---------------------
    @Override
    public void create(CreatePostDTO dto) {
        //1.读取当前用户
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        //2.组装帖子实体
        Post post = new Post();
        post.setUserId(userId);
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setImageUrl(dto.getImageUrl());
        post.setStatus(PostStatus.ENABLE);
        post.setLikeCount(0);
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());

        //3.保存帖子
        postMapper.insert(post);
    }

    //--------------------查询帖子接口实现(公开)---------------------
    @Override
    public PageResult<PostVO> page(PagePostDTO dto) {
        //1.创建 MyBatis-Plus 的分页对象
        Page<Post> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        //2.执行分页查询
        Page<Post> result = postMapper.selectPage(
                page,
                Wrappers.<Post>lambdaQuery()
                        .ne(Post::getUserId, adminUserId)
                        .eq(Post::getStatus, PostStatus.ENABLE)
                        .orderByDesc(Post::getCreateTime)  //orderbydesc: 倒序排列
        );

        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toPostVO).toList());
    }

    //--------------------公告分页接口实现(公开)---------------------
    @Override
    public PageResult<PostVO> noticePage(PagePostDTO dto) {
        //1.创建 MyBatis-Plus 的分页对象
        Page<Post> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        //2.执行分页查询（仅管理员帖子，作为公告）
        Page<Post> result = postMapper.selectPage(
                page,
                Wrappers.<Post>lambdaQuery()
                        .eq(Post::getUserId, adminUserId)
                        .eq(Post::getStatus, PostStatus.ENABLE)
                        .orderByDesc(Post::getCreateTime)
        );

        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toPostVO).toList());
    }

    //--------------------我的帖子接口实现(私有)---------------
    @Override
    public PageResult<PostVO> myPage(PagePostDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        Page<Post> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        Page<Post> result = postMapper.selectPage(
                page,
                Wrappers.<Post>lambdaQuery()
                        .eq(Post::getUserId, userId)
                        .eq(Post::getStatus, PostStatus.ENABLE)
                        .orderByDesc(Post::getCreateTime)
        );

        return new PageResult<>(result.getTotal(), result.getRecords().stream().map(this::toPostVO).toList());
    }

    //--------------------帖子详情接口实现---------------------
    @Override
    public PostVO detail(Long id) {

        Post post = postMapper.selectById(id);

        //1.判断帖子是否存在
        if(post == null){
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }
        //2.判断帖子是否公开
        if (post.getStatus() != PostStatus.ENABLE) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }
        //2.返回结果
        return toPostVO(post);
    }

    //--------------------修改帖子接口实现---------------------
    @Override
    public void updateMyPost(Long id, UpdatePostDTO dto) {
        // 1.登录校验
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        // 2.帖子存在校验
        Post dbPost = postMapper.selectById(id);
        if (dbPost == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }

        // 3.状态校验（已删除不可修改）
        if (dbPost.getStatus() != PostStatus.ENABLE) {
            throw new BusinessException(ErrorCode.POST_STATUS_ERROR, "帖子状态错误");
        }

        // 4.权限校验（只能改自己的）
        if (!userId.equals(dbPost.getUserId())) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "无权修改他人帖子");
        }

        // 5.更新字段
        Post updatePost = new Post();
        updatePost.setId(id);
        updatePost.setTitle(dto.getTitle());
        updatePost.setContent(dto.getContent());
        updatePost.setImageUrl(dto.getImageUrl());
        updatePost.setUpdateTime(LocalDateTime.now());

        postMapper.updateById(updatePost);
    }


    //--------------------删除帖子接口实现---------------------
    @Override
    public void deleteMyPost(Long id) {
        //1.登陆校验
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "当前未登录或token缺失");
        }

        //2.判断帖子是否存在
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }

        //3.判断当前用户是否是帖子的作者
        if (!post.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.NO_PERMISSION, "无权限删除该帖子");
        }

        //4.删除帖子
        Post updatePost = new Post();
        updatePost.setId(id);
        updatePost.setStatus(PostStatus.DISABLE);
        postMapper.updateById(updatePost);
    }

    //--------------------私有工具方法---------------------
    // 1.转换 Post 到 PostVO
    private PostVO toPostVO(Post post) {
        PostVO vo = new PostVO();
        vo.setId(post.getId());
        vo.setUserId(post.getUserId());
        vo.setTitle(post.getTitle());
        vo.setContent(post.getContent());
        vo.setImageUrl(post.getImageUrl());
        vo.setLikeCount(post.getLikeCount());
        vo.setCreateTime(post.getCreateTime());
        return vo;
    }
}

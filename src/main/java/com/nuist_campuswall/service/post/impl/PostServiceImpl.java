package com.nuist_campuswall.service.post.impl;

import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import com.nuist_campuswall.domain.enums.PostStatus;
import com.nuist_campuswall.domain.post.Post;
import com.nuist_campuswall.dto.post.CreatePostDTO;
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

    //--------------------发帖接口实现---------------------
    @Override
    public void create(CreatePostDTO dto) {
        //1.读取当前用户
        Long userId = UserContext.getUserId();
        if(userId == null){
            throw new BusinessException(ErrorCode.UNAUTHORIZED,"当前未登录或token缺失");
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

}

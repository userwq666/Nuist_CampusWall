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
import com.nuist_campuswall.mapper.post.PostMapper;
import com.nuist_campuswall.security.UserContext;
import com.nuist_campuswall.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;

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

    //--------------------查询帖子接口实现---------------------
    @Override
    public PageResult<PostVO> page(PagePostDTO dto) {
        //1.创建 MyBatis-Plus 的分页对象
        Page<Post> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            
        //2.执行分页查询
        Page<Post> result = postMapper.selectPage(
                page,
                Wrappers.<Post>lambdaQuery()
                        .orderByDesc(Post::getCreateTime)  //orderbydesc: 倒序排列
        );
            
        //3.将 MyBatis-Plus 的 Page 对象转换为 PageResult 对象
        List<PostVO> records = result.getRecords().stream().map(this::toPostVO).toList();
        return new PageResult<>(result.getTotal(), records);
    }

    //--------------------帖子详情接口实现---------------------
    @Override
    public PostVO detail(Long id) {

        Post post = postMapper.selectById(id);

        //1.判断帖子是否存在
        if(post == null){
            throw new BusinessException(ErrorCode.POST_NOT_FOUND, "帖子不存在");
        }
        
        //2.返回结果
        return toPostVO(post);
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

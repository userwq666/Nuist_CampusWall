package com.nuist_campuswall.service.post;

import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.dto.post.CreatePostDTO;
import com.nuist_campuswall.dto.post.PagePostDTO;
import com.nuist_campuswall.dto.post.PostVO;

public interface PostService {
    // 发帖接口
    void create(CreatePostDTO dto);

    // 查询帖子分页接口
    PageResult<PostVO> page(PagePostDTO dto);

    // 查询帖子详情接口
    PostVO detail(Long id);
}

package com.nuist_campuswall.service.post;

import com.nuist_campuswall.dto.post.CreatePostDTO;

public interface PostService {
    //发帖接口
    void create(CreatePostDTO dto);
}

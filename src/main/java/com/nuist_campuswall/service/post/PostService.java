package com.nuist_campuswall.service.post;

import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.dto.post.CreatePostDTO;
import com.nuist_campuswall.dto.post.PagePostDTO;
import com.nuist_campuswall.dto.post.PostVO;
import com.nuist_campuswall.dto.post.UpdatePostDTO;

public interface PostService {
    //发帖接口
    void create(CreatePostDTO dto);

    //查询帖子分页接口(公开)
    PageResult<PostVO> page(PagePostDTO dto);

    //公告分页接口
    PageResult<PostVO> noticePage(PagePostDTO dto);

    //我的帖子接口(私有)
    PageResult<PostVO> myPage(PagePostDTO dto);

    //查询帖子详情接口
    PostVO detail(Long id);

    //修改帖子接口
    void updateMyPost(Long id, UpdatePostDTO dto);

    //删除帖子接口
    void deleteMyPost(Long id);
}

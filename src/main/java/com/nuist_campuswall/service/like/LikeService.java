package com.nuist_campuswall.service.like;

import com.nuist_campuswall.dto.like.LikeDTO;
import com.nuist_campuswall.dto.like.UnlikeDTO;


public interface LikeService {
    //点赞接口
    public void like(LikeDTO dto);
    //取消点赞接口
    public void unlike(UnlikeDTO dto);
}

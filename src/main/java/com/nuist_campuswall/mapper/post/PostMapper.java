package com.nuist_campuswall.mapper.post;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuist_campuswall.domain.post.Post;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface PostMapper extends BaseMapper<Post> {
    //点赞数加1
    @Update("update post set like_count = like_count + 1 where id = #{id}")
    void incrLikeCount(@Param("id") Long id);

    //点赞数减1                             //case when 满足条件则减1，否则为0
    @Update("update post set like_count = case when like_count>0 then like_count-1 else 0 end where id=#{id}")
    void decrLikeCount(@Param("id") Long id);
}

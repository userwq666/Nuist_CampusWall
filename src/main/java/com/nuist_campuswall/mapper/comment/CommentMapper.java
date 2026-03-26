package com.nuist_campuswall.mapper.comment;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nuist_campuswall.domain.comment.Comment;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface CommentMapper extends BaseMapper<Comment> {
    // 点赞数+1
    @Update("UPDATE comment SET like_count = like_count + 1 WHERE id = #{id}")
    void incrLikeCount(@Param("id") Long id);
    // 点赞数-1
    @Update("UPDATE comment SET like_count = CASE WHEN like_count > 0 THEN like_count - 1 ELSE 0 END WHERE id = #{id}")
    void decrLikeCount(@Param("id") Long id);

}

package com.nuist_campuswall.service.comment;

import com.nuist_campuswall.dto.comment.CommentVO;
import com.nuist_campuswall.dto.comment.CreateCommentDTO;
import com.nuist_campuswall.dto.comment.PageCommentDTO;
import com.nuist_campuswall.dto.common.PageResult;

public interface CommentService {
    // 创建评论接口
    void create(CreateCommentDTO dto);

    // 查询评论分页接口
    PageResult<CommentVO> page(PageCommentDTO dto);
}

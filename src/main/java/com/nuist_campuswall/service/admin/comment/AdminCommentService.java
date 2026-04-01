package com.nuist_campuswall.service.admin.comment;

import com.nuist_campuswall.dto.admin.AdminCommentDetailVO;
import com.nuist_campuswall.dto.admin.AdminCommentPageDTO;
import com.nuist_campuswall.dto.admin.AdminCommentVO;
import com.nuist_campuswall.dto.common.PageResult;

public interface AdminCommentService {
    //管理员评论分页接口
    PageResult<AdminCommentVO> pageComment(AdminCommentPageDTO dto);

    //管理员评论详情接口
    AdminCommentDetailVO detail(Long id);

    //管理员启用评论接口
    void enableComment(Long id);

    //管理员禁用评论接口
    void disableComment(Long id);
}

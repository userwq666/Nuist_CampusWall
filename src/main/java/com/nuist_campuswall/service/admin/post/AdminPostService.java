package com.nuist_campuswall.service.admin.post;

import com.nuist_campuswall.dto.admin.AdminPostDetailVO;
import com.nuist_campuswall.dto.admin.AdminPostPageDTO;
import com.nuist_campuswall.dto.admin.AdminPostVO;
import com.nuist_campuswall.dto.common.PageResult;

public interface AdminPostService {
    //管理员帖子分页接口
    PageResult<AdminPostVO> page(AdminPostPageDTO dto);

    //管理员帖子详情接口
    AdminPostDetailVO detail(Long id);

    //管理员启用帖子接口
    void  enablePost(Long id);

    //管理员禁用帖子接口
    void  disablePost(Long id);
}

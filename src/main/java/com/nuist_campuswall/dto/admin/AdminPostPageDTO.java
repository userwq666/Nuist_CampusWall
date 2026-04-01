package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.PostStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AdminPostPageDTO {
    //页码
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum=1;
    //每页数量
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 10, message = "每页数量最大为10")
    private Integer pageSize=5;

    //发帖人ID
    private Long userId;

    //状态
    private PostStatus status;
}

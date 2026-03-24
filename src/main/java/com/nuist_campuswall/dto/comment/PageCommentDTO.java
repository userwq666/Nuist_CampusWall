package com.nuist_campuswall.dto.comment;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PageCommentDTO {
    //目标帖子ID
    @NotNull(message = "目标帖子ID不能为空")
    private Long postId;

    //页码
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum=1;

    //每页数量
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 10, message = "每页数量最大为10")
    private Integer pageSize=5;
}

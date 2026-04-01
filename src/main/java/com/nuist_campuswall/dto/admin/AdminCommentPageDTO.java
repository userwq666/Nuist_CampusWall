package com.nuist_campuswall.dto.admin;

import com.nuist_campuswall.domain.enums.CommentStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class AdminCommentPageDTO {
    //页码
    @Min(value = 1, message = "页码最小为1")
    private Integer pageNum = 1;

    //每页数量
    @Min(value = 1, message = "每页数量最小为1")
    @Max(value = 10, message = "每页数量最大为10")
    private Integer pageSize = 5;

    //目标帖子ID（可选）
    private Long postId;

    //评论人ID（可选）
    private Long userId;

    //评论状态（可选）
    private CommentStatus status;
}

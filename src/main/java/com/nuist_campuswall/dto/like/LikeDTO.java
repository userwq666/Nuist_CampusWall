package com.nuist_campuswall.dto.like;

import com.nuist_campuswall.domain.enums.TargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LikeDTO {
    // 点赞目标 ID
    @NotNull(message = "目标 ID 不能为空")
    private Long targetId;
    // 点赞目标类型
    @NotNull(message = "目标类型不能为空")
    private TargetType targetType;
}

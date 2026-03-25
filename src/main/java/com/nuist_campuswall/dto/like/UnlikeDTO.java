package com.nuist_campuswall.dto.like;

import com.nuist_campuswall.domain.enums.TargetType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UnlikeDTO {
    @NotNull(message = "目标类型不能为空")
    private TargetType targetType;
    @NotNull(message = "目标 ID 不能为空")
    private Long targetId;
}

package com.nuist_campuswall.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdatePostDTO {
    @NotBlank(message = "标题不能为空")
    @Size(max = 50, message = "标题长度不能超过50")
    private String title;

    @NotBlank(message = "正文不能为空")
    @Size(max = 10000, message = "正文长度不能超过10000")
    private String content;

    private String imageUrl;
}

package com.nuist_campuswall.controller.like;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.dto.like.LikeDTO;
import com.nuist_campuswall.dto.like.UnlikeDTO;
import com.nuist_campuswall.service.like.LikeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/api/like")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping("/do")
    public Result<String> like(@Valid @RequestBody LikeDTO dto){
        likeService.like(dto);
        return Result.success("点赞成功");
    }

    @PostMapping("/undo")
    public Result<String> unlike(@Valid @RequestBody UnlikeDTO dto){
        likeService.unlike(dto);
        return Result.success("取消点赞成功");
    }
}

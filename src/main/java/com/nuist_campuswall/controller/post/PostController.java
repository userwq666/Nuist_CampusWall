package com.nuist_campuswall.controller.post;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.dto.post.CreatePostDTO;
import com.nuist_campuswall.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//帖子控制器
@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    //发帖接口
    @PostMapping("/create")        //@Valid 表示参数校验
    public Result<String> create(@Valid @RequestBody CreatePostDTO dto){
        postService.create(dto);
        return Result.success("帖子创建成功");
    }
}

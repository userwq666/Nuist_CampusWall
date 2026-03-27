package com.nuist_campuswall.controller.post;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.dto.post.CreatePostDTO;
import com.nuist_campuswall.dto.post.PagePostDTO;
import com.nuist_campuswall.dto.post.PostVO;
import com.nuist_campuswall.dto.post.UpdatePostDTO;
import com.nuist_campuswall.service.post.PostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    //查询帖子接口
    @GetMapping("/page")                //@ModelAttribute 告诉SpringMVC，这个参数是一个对象，而不是一个字符串
    public Result<PageResult<PostVO>> page(@Valid @ModelAttribute PagePostDTO dto){
        return Result.success(postService.page(dto));
    }

    //查询公告接口
    @GetMapping("/notice/page")
    public Result<PageResult<PostVO>> noticePage(@Valid @ModelAttribute PagePostDTO dto){
        return Result.success(postService.noticePage(dto));
    }

    //我的帖子接口
    @GetMapping("/my/page")
    public Result<PageResult<PostVO>> myPage(@Valid @ModelAttribute PagePostDTO dto){
        return Result.success(postService.myPage(dto));
    }

    //帖子详情接口
    @GetMapping("/{id}")       //@PathVariable 表示路径参数
    public Result<PostVO> detail(@PathVariable Long id){
        return Result.success(postService.detail(id));
    }

    //修改帖子接口
    @PostMapping("/update/{id}")
    public Result<String> update(@PathVariable Long id, @Valid @RequestBody UpdatePostDTO dto){
        postService.updateMyPost(id, dto);
        return Result.success("帖子修改成功");
    }

    //删除帖子接口
    @PostMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id){
        postService.deleteMyPost(id);
        return Result.success("帖子删除成功");
    }
}

package com.nuist_campuswall.controller.comment;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.dto.comment.CommentVO;
import com.nuist_campuswall.dto.comment.CreateCommentDTO;
import com.nuist_campuswall.dto.comment.PageCommentDTO;
import com.nuist_campuswall.dto.common.PageResult;
import com.nuist_campuswall.service.comment.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    //创建评论接口
    @PostMapping("/create")           //@requestBody 告诉SpringMVC，这个参数是一个对象
    public Result<String> create(@Valid @RequestBody CreateCommentDTO dto) {
        commentService.create(dto);
        return Result.success("评论创建成功");
    }

    //查询评论接口
    @GetMapping("/page")
    public Result<PageResult<CommentVO>> page(@Valid @ModelAttribute PageCommentDTO dto) {
        return Result.success(commentService.page(dto));
    }

    //我的评论接口
    @GetMapping("/my/page")
    public Result<PageResult<CommentVO>> myPage(@Valid @ModelAttribute PageCommentDTO dto) {
        return Result.success(commentService.myPage(dto));
    }


    //删除评论接口
    @PostMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        commentService.deleteMyComment(id);
        return Result.success("评论删除成功");
    }
}

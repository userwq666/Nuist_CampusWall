package com.nuist_campuswall.controller.account;

import com.nuist_campuswall.common.Result;
import com.nuist_campuswall.dto.account.LoginDTO;
import com.nuist_campuswall.dto.account.LoginRespVO;
import com.nuist_campuswall.dto.account.LoginVO;
import com.nuist_campuswall.dto.account.RegisterDTO;
import com.nuist_campuswall.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//账户控制器
@RestController                         //接口控制器,返回json文件
@RequestMapping("/api/account")     //给类加上统一前缀
@RequiredArgsConstructor               //该注解会自动为类中的所有final字段创建一个构造函数
public class AccountController {

    private final AccountService accountService;

    //注册接口
    @PostMapping("/register")       //指定这个方法接收post请求
    public Result<String> register(@RequestBody RegisterDTO dto){        //把json文件转成dto对象
        accountService.register(dto);
        return Result.success("注册成功");
    }

    //登录接口
    @PostMapping("/login")
    public Result<LoginRespVO> login(@RequestBody LoginDTO dto){      //把json文件转成dto对象
        LoginRespVO respVO = accountService.login(dto);
        return Result.success(respVO);
    }

    //我的接口
    @GetMapping("/me")
    public Result<LoginVO> me(){
        return Result.success(accountService.me());
    }
}

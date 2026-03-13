package com.nuist_campuswall.controller.account;

import com.nuist_campuswall.dto.account.RegisterDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.transform.Result;

@RestController                         //接口控制器,返回json文件
@RequestMapping("/api/account")         //给类加上统一前缀
public class AccountController {
    @PostMapping("/register")       //指定这个方法接收post请求
    public String register(@RequestBody RegisterDTO dto){        //把josn文件转成dto对象
        return "注册接口联通";
    }
}

package com.nuist_campuswall.service.account;

import com.nuist_campuswall.dto.comment.account.LoginDTO;
import com.nuist_campuswall.dto.comment.account.LoginRespVO;
import com.nuist_campuswall.dto.comment.account.LoginVO;
import com.nuist_campuswall.dto.comment.account.RegisterDTO;

public interface AccountService {
    //注册接口
    void register(RegisterDTO dto);

    //登录接口
    LoginRespVO login(LoginDTO dto);

    //我的接口
    LoginVO me();
}


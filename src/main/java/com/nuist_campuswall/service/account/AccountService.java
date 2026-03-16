package com.nuist_campuswall.service.account;

import com.nuist_campuswall.dto.account.LoginDTO;
import com.nuist_campuswall.dto.account.LoginVO;
import com.nuist_campuswall.dto.account.RegisterDTO;

public interface AccountService {
    //注册接口
    void register(RegisterDTO dto);

    //登录接口
    LoginVO login(LoginDTO dto);
}


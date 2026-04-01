package com.nuist_campuswall.service.account;

import com.nuist_campuswall.dto.account.LoginDTO;
import com.nuist_campuswall.dto.account.LoginRespVO;
import com.nuist_campuswall.dto.account.LoginVO;
import com.nuist_campuswall.dto.account.MyInfoDTO;
import com.nuist_campuswall.dto.account.RegisterDTO;

public interface AccountService {
    //注册接口
    void register(RegisterDTO dto);

    //登录接口
    LoginRespVO login(LoginDTO dto);

    //我的接口
    LoginVO myInfo();

    //修改我的信息接口
    void updateMyInfo(MyInfoDTO dto);
}


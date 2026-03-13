package com.nuist_campuswall.service.account;

import com.nuist_campuswall.dto.account.RegisterDTO;

public interface AccountService {
    void register(RegisterDTO dto);           // 注册方法 , 参数为注册信息
}

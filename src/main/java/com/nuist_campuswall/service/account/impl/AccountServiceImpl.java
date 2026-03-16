package com.nuist_campuswall.service.account.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.domain.enums.Role;
import com.nuist_campuswall.domain.enums.UserStatus;
import com.nuist_campuswall.domain.user.User;
import com.nuist_campuswall.dto.account.LoginDTO;
import com.nuist_campuswall.dto.account.LoginVO;
import com.nuist_campuswall.dto.account.RegisterDTO;
import com.nuist_campuswall.mapper.user.UserMapper;
import com.nuist_campuswall.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

// 实现类
@Service                     // 该注解会将当前类注册为一个服务，并自动创建一个代理对象
@RequiredArgsConstructor   //该注解会自动为类中的所有final字段创建一个构造函数
public class AccountServiceImpl implements AccountService {

    private final UserMapper userMapper;

    //--------------------注册接口实现--------------------
    @Override
    public void register(RegisterDTO dto) {
        // 1. 检查用户名是否已存在
        long usernameCount = userMapper.selectCount(              // selectCount() 方法会返回查询结果数量
                Wrappers.<User>lambdaQuery()                      //la
                        .eq(User::getUsername, dto.getUsername())
        );
        if(usernameCount > 0){
            throw new BusinessException(401,"用户名已存在");          // 抛出运行时异常
        }

        // 2. 检查邮箱是否已存在
        long emailCount = userMapper.selectCount(
                Wrappers.<User>lambdaQuery()
                        .eq(User::getEducationEmail, dto.getEducationEmail())
        );
        if(emailCount > 0){
            throw new BusinessException(402,"邮箱已存在");          // 抛出运行时异常
        }

        //3.密码加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();    // 创建密码编码器
        String encodedPassword = encoder.encode(dto.getPassword());     // 对密码进行加密

        //4.保存用户信息
        User user = new User();
        //从dto中获取用户信息
        user.setUsername(dto.getUsername());
        user.setPassword(encodedPassword);
        user.setNickname(dto.getNickname());
        user.setEducationEmail(dto.getEducationEmail());
        //设置用户角色和状态
        user.setRole(Role.USER);   // 普通用户
        user.setStatus(UserStatus.ENABLE);   // 启用
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        //5.插入数据库
        userMapper.insert(user);

    }

    //--------------登录接口实现--------------------
    @Override
    public LoginVO login(LoginDTO dto) {
        // 1. 查询用户
        User user = userMapper.selectOne(
                Wrappers.<User>lambdaQuery()
                        .eq(User::getUsername,dto.getUsername())
        );

        // 2. 检查用户是否存在
        if(user == null){
            throw new BusinessException(403,"用户名不存在");
        }

        //3.检查账号状态
        if(user.getStatus()==UserStatus.DISABLE){
            throw new BusinessException(404,"用户已被禁用");
        }

        //4.密码校验
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        //encoder.matches方法：判断密码是否匹配 参数1：用户输入的密码，参数2：数据库中的密码
        if(!encoder.matches(dto.getPassword(),user.getPassword())){
            throw new BusinessException(405,"密码错误");
        }

        //5.映射登录视图对象LoginVO
        LoginVO vo = new LoginVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setImageUrl(user.getImageUrl());
        vo.setEducationEmail(user.getEducationEmail());
        vo.setRole(user.getRole().name());      // 枚举转字符串
        vo.setStatus(user.getStatus().name());  // 枚举转字符串

        //6.返回结果
        return vo;
    }
}

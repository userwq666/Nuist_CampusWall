package com.nuist_campuswall.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


@Component             // 表示该类被Spring管理
@AllArgsConstructor
public class initCheck implements ApplicationRunner {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Integer userTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables " +
                "WHERE table_schema = DATABASE() AND table_name = 'user'", 
                Integer.class
        );

        Integer adminTable = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM `user` WHERE username='admin'",
                        Integer.class
        );
        
        if (userTable != null && userTable >= 0) {
            System.out.println("[INIT] SQL初始化成功!");
        } else {
            System.out.println("[INIT] SQL初始化失败!");
        }

        if (adminTable != null && adminTable > 0){
            System.out.println("[INIT] 管理员账号:admin");
            System.out.println("[INIT] 管理员密码:123456");
        }else  {
            System.out.println("[INIT] 初始化管理员失败!");
        }
    }
}

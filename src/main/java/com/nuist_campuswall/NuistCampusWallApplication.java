package com.nuist_campuswall;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.nuist_campuswall.mapper")      // 指定mapper接口的包名
public class NuistCampusWallApplication {

    public static void main(String[] args) {

        SpringApplication.run(NuistCampusWallApplication.class, args);
    }

}

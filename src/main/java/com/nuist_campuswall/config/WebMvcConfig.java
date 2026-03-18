package com.nuist_campuswall.config;

import com.nuist_campuswall.security.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// web mvc配置类
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {   //webconfigurer接口，用于配置拦截器

    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Override            //addInterceptors方法，用于添加拦截器,参数为拦截器注册器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)         //添加拦截器
                .addPathPatterns("/api/**")                 //添加拦截路径
                .excludePathPatterns(                       //添加排除路径
                        "/api/account/register",
                        "/api/account/login"
                );
    }
}

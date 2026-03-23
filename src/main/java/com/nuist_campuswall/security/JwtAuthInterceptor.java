package com.nuist_campuswall.security;

import com.nuist_campuswall.common.BusinessException;
import com.nuist_campuswall.common.ErrorCode;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

//拦截器
@Component        //把类交给spring管理
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;

    @Override      //preHandle方法作用是拦截请求，返回true表示继续处理请求，返回false表示不处理请求  参数request和response是请求和响应对象  参数handler是处理请求的handler对象
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        //1.读取请求头
        String auth = request.getHeader("Authorization");             //Authorization是认证头
        if (auth == null || !auth.startsWith("Bearer ")){                //auth.startsWith("Bearer")方法判断auth是否以Bearer开头
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或token缺失");
       }

        //2.截取token
        String token = auth.substring(7);           //auth.substring(7)方法截取token

        try {
            //3.解析token
            Claims claims = jwtUtil.parseToken(token);           //jwtUtil.parseToken(token)方法解析token

            //取出id放入用户上下文
            Object userIdobj = claims.get("userId");            //claims.get("userId")方法取出id放入对象中
            if(userIdobj == null){
                throw new BusinessException(ErrorCode.TOKEN_INVALID, "token无效");
            }

            Long userId;
            if(userIdobj instanceof Integer){                           //判断id的类型
                userId = ((Integer)userIdobj).longValue();             //id类型为Integer时，转为Long
            }else if(userIdobj instanceof Long){                       //id类型为Long时,直接赋值给userId
                userId = (Long) userIdobj;
            }else {                                                   //id类型为其他类型时，转为Long
                userId = Long.parseLong(userIdobj.toString());
            }

            UserContext.setUserId(userId);
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.TOKEN_INVALID, "token无效或已过期");
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        //请求完成,清空用户信息
        UserContext.clear();
    }
}

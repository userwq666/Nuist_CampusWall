package com.nuist_campuswall.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

// jwt工具类
@Component           // 把类交给spring管理
public class JwtUtil {
    // 从配置文件中获取密钥
    @Value("${jwt.secret}")
    private String secret;
    //从配置文件中获取过期时间
    @Value("${jwt.expire-seconds}")
    private Long expireSeconds;
    //从配置文件中获取发行方
    @Value("${jwt.issuer}")
    private String issuer;

    //构件签名密钥
    private SecretKey getKey() {    //SecretKey是JWT的签名密钥
        //keys.hmacShaKeyFor方法：创建一个HMAC密钥 参数为密钥的字节数组
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    //生成token
    public String generateToken(Map<String,Object> claims) {     //MAP<String,Object>Claims参数为Claims对象
        Instant now = Instant.now();
        Instant expireAt = now.plusSeconds(expireSeconds);  // 设置过期时间
        return Jwts.builder()        //jwts.builder()创建一个jwt构建器
                .claims(claims)        // 设置Claims
                .issuer(issuer)        // 设置发行方
                .issuedAt(Date.from(now))   // 设置发行时间
                .expiration(Date.from(expireAt))     // 设置过期时间
                .signWith(getKey())             // 设置签名密钥
                .compact();              //生成 token
    }

    // 解析token
    public Claims parseToken(String token) {
        return Jwts.parser()     // jwts.parser()创建一个jwt解析器
                .verifyWith(getKey())   // 验证签名密钥
                .requireIssuer(issuer)  // 要求发行方
                .build()                  // 构建解析器
                .parseSignedClaims(token)   // 解析token
                .getPayload();              // 获取Claims
    }
}

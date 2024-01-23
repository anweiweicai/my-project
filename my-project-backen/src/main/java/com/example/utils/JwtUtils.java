package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;//从配置文件获取key值

    @Value("${spring.security.jwt.expire}")
    int expire;//从配置文件获取key值

    public String creatJwt(UserDetails details, int id, String username){//创建jwt令牌, 传入用户id和用户名
        Algorithm algorithm = Algorithm.HMAC256(key);//加密
        return JWT.create()
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(this.expireTime())//过期时间
                .withIssuedAt(new Date())//Token 颁发时间
                .sign(algorithm);//签名
    }

    public Date expireTime(){

        Calendar calendar = Calendar.getInstance();//日历功能, 获取当前时间
        calendar.add(Calendar.HOUR, expire * 24);//当前时间基础上加了7天
        return calendar.getTime();//返回7天以后的时刻

    }
}

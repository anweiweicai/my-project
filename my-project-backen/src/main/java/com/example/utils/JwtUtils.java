package com.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.entity.dto.Account;
import com.example.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtils {

    @Value("${spring.security.jwt.key}")
    String key;//从配置文件获取key值

    @Value("${spring.security.jwt.expire}")
    int expire;//从配置文件获取key值

    @Resource
    StringRedisTemplate template;

    @Resource
    AccountService service;

    public boolean invalidateJwt(String headerToken){//让令牌失效, 并返回是否成功
        String token = this.convertToken(headerToken);
        if (token == null) return false;
        Algorithm algorithm = Algorithm.HMAC256(key);
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();
        try {
            DecodedJWT jwt = jwtVerifier.verify(token);
            String id = jwt.getId();
            return deleteToken(id, jwt.getExpiresAt());
        }catch (JWTVerificationException e){
            return false;
        }
    }

    private boolean deleteToken(String uuid, Date time){//time 表示过期时间
        if (this.isInvalidToken(uuid)) return false;
        Date now = new Date();
        long expire = Math.max(time.getTime() - now.getTime(), 0);//计算过期时间 若为负数,则为0
        template.opsForValue().set(Const.JWT_BLACK_LIST + uuid, "", expire, TimeUnit.MILLISECONDS);//存到缓存中
        return true;
    }

    private boolean isInvalidToken(String uuid){//判断token是否已经失效了
        return Boolean.TRUE.equals(template.hasKey(Const.JWT_BLACK_LIST + uuid));//uuid在黑名单中, 那这个token就失效了
    }
    public DecodedJWT resolveJwt(String headerToken){//验证token, 并解码token
        String token = this.convertToken(headerToken);
        if (token == null) return null;
        Algorithm algorithm = Algorithm.HMAC256(key);//加密
        JWTVerifier jwtVerifier = JWT.require(algorithm).build();//指定了需要使用HMAC256算法对JWT进行签名验证
//      DecodedJWT verify = jwtVerifier.verify(token);  验证token有效性, 将token与HMAC256(key)进行比对, 返回从令牌中解码出来的信息，比如令牌的过期时间、颁发者、以及任何其他声明
        try {
            DecodedJWT verify = jwtVerifier.verify(token);
            if (this.isInvalidToken(verify.getId())) return null;//判断token是否失效
            Date expiresAt = verify.getExpiresAt();//获取过期日期
            return new Date().after(expiresAt) ? null : verify;//判断当前的日期和时间是否晚于`expiresAt`所代表的日期和时间
        }catch (JWTVerificationException e){//运行时异常
            return null;
        }
    }

    public String creatJwt(UserDetails details, int id, String username){//创建jwt令牌, 传入用户id和用户名
        Algorithm algorithm = Algorithm.HMAC256(key);//加密
        return JWT.create()
                .withJWTId(UUID.randomUUID().toString())//令牌的随机ID
                .withClaim("id",id)
                .withClaim("name",username)
                .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .withExpiresAt(this.expireTime())//过期时间
                .withIssuedAt(new Date())//Token 颁发时间
                .sign(algorithm);//签名
    }
//    .withClaim("authorities", details.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
//    1.`details.getAuthorities()` 是某个用户的权限信息，返回一个权限列表（通常是`GrantedAuthority` 对象的列表）。
//    2. `stream()` 方法将列表转换成了一个流，使得可以对列表中的元素进行一系列的操作。
//    3.`map(GrantedAuthority::getAuthority)` 对流中的每一个权限对象（`GrantedAuthority`）执行`getAuthority` 方法，获取其权限字符串，将这些权限字符串映射成一个新的流。
//    4.`toList()` 将上一步映射后的流转换为一个列表。
    public Date expireTime(){
        Calendar calendar = Calendar.getInstance();//日历功能, 获取当前时间
        calendar.add(Calendar.HOUR, expire * 24);//当前时间基础上加了7天
        return calendar.getTime();//返回7天以后的时刻
    }

    public UserDetails toUser(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        Account account = service.findAccountByNameOrEmail(claims.get("name").asString());
        return User
                .withUsername(claims.get("name").asString())//获取属性名字应该与jwt创建时的属性名相同 即JWT.create()里定义的属性
                .password(account.getPassword())
                .authorities(claims.get("authorities").asArray(String.class))
        //        1.`claims.get("authorities")` 表示从声明中获取名为 "authorities" 的值。
        //        2. `asArray(String.class)` 表示将这个值转换为一个字符串数组。这个数组中包含了用户的权限信息。
                .build();
    }

    public Integer toId(DecodedJWT jwt){
        Map<String, Claim> claims = jwt.getClaims();
        return claims.get("id").asInt();
    }

    private String convertToken(String headerToken){
        //判断Token是否合法
        if (headerToken == null || !headerToken.startsWith("Bearer "))//这里的字符有7个
            return null;
        return headerToken.substring(7);//从7位置开始切割
    }
}

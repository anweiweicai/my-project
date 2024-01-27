package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class WebConfiguration {
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
//    这段代码是将`BCryptPasswordEncoder` 实例化为一个Spring Bean，并注册到应用程序的上下文中。
//    当Spring Security需要对密码进行加密或解密时，它会自动检测到这个`BCryptPasswordEncoder` Bean，并将其用作密码编码器。
}

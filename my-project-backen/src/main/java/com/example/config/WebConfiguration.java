package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class WebConfiguration {
    @Bean
    BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
//    这段代码是将`BCryptPasswordEncoder` 实例化为一个Spring Bean，并注册到应用程序的上下文中。
//    当Spring Security需要对密码进行加密或解密时，它会自动检测到这个`BCryptPasswordEncoder` Bean，并将其用作密码编码器。

    @Bean // 注入RestTemplate
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    // RestTemplate通常用于在Java应用程序中进行HTTP请求。
    // 该类提供了一种简单的方法来发送HTTP请求，包括GET、POST、PUT、DELETE等方法。
    // RestTemplate是Spring框架提供的用于发送HTTP请求的客户端类。它可以执行GET、POST、PUT、DELETE等HTTP操作，并处理响应。通常用于与RESTful API进行交互。
}

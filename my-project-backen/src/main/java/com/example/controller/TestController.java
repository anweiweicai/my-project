package com.example.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController//所有返回都是json格式或xml格式, 即返回数据而不是视图
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/hello")
    public String test(){
        return "Hello World!";
    }
    //    定义了一个 RESTful 服务的控制器类`TestController`
    //    该类中有一个处理HTTP GET请求的方法`test`
    //    当客户端发起 GET 请求到"/api/test/hello"时
    //    将会返回"Hello World!"作为响应。
}

package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService service;
    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double latitude, double longitude) {
        WeatherVO vo = service.fetchWeather(latitude, longitude);
        return vo == null ?
                RestBean.failure(400,"获取地理位置信息和天气信息失败, 请联系工作人员!") :
                RestBean.success(vo);
    }
}

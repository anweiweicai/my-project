package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.response.TopicTypeVO;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService service;

    @Resource
    TopicService topicService;
    @GetMapping("/weather")
    public RestBean<WeatherVO> weather(double latitude, double longitude) {
        WeatherVO vo = service.fetchWeather(latitude, longitude);
        return vo == null ?
                RestBean.failure(400,"获取地理位置信息和天气信息失败, 请联系工作人员!") :
                RestBean.success(vo);
    }

    @GetMapping("/types")
    public RestBean<List<TopicTypeVO>> listTypes() {
        List<TopicTypeVO> vos = topicService.listTypes().stream()
                .map(topicType -> {
                    TopicTypeVO vo = new TopicTypeVO();
                    BeanUtils.copyProperties(topicType, vo);
                    return vo;
                })
                .toList();
        return RestBean.success(vos);
    }
}

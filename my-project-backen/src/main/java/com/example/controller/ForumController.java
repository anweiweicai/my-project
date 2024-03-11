package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.entity.vo.response.TopicTypeVO;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {
    @Resource
    WeatherService service;

    @Resource
    TopicService topicService;

    @Resource
    ControllerUtils utils;
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

    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVO vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id) {
        return utils.messageHandle(() -> topicService.createTopic(id, vo));
    }

    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVO>> listTopics(@RequestParam @Min(0) int page,
                                                     @RequestParam @Min(0) int type) {
        return RestBean.success(topicService.listTopicByPage(page, type));
    }
}

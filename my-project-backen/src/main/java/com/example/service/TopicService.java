package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.TopicType;

import java.util.List;

public interface TopicService extends IService<TopicType> {
    List<TopicType> listTypes();
}

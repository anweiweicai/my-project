package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.TopicType;
import com.example.mapper.TopicTypeMapper;
import com.example.service.TopicService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicTypeMapper, TopicType> implements TopicService {
    @Resource
    TopicTypeMapper mapper;
    @Override
    public List<TopicType> listTypes() {
        return mapper.selectList(null); // 返回所有记录
    }
}

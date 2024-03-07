package com.example.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.mapper.TopicMapper;
import com.example.mapper.TopicTypeMapper;
import com.example.service.TopicService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    TopicTypeMapper mapper;

    @Resource
    FlowUtils flowUtils;

    private Set<Integer> types;
    @PostConstruct
    private void initTypes(){
        types = this.listTypes()
                .stream()
                .map(TopicType::getId)
                .collect(Collectors.toSet());
    }
//   private Set<Integer> types  = this.listTypes() 错误的赋值方式, 在属性定义的同时调用实例进行初始化, 属性定义是在实例初始化之前进行的
//                    .stream()
//                .map(TopicType::getId)
//                .collect(Collectors.toSet());

    @Override
    public List<TopicType> listTypes() {
        return mapper.selectList(null); // 返回所有记录
    }

    @Override
    public String createTopic(int uid, TopicCreateVO vo) {
        String key = Const.FORUM_TOPIC_CREATE_COUNTER + uid;
        if (!textLimitCheck(vo.getContent())) return "文章内容超过限定字数, 请重新编写!";
        if (!types.contains(vo.getType())) return "文章类型非法!";
        if (!flowUtils.limitPeriodCounterCheck(key, 5, 3600)) return "您在一小时内发文超过5篇, 过于频繁, 请稍后再试!";
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if (this.save(topic)) {
            return null;
        }else {
            return "内部错误, 请联系管理员!";
        }
    }

    /**
     * 检查文本长度是否超过限定字数 20000字
     * @param object
     * @return 超过返回false, 未超过返回true
     */
    private boolean textLimitCheck(JSONObject object) {
        if (object == null) return false;
        long length = 0;
        for (Object op : object.getJSONArray("ops")){
            length += JSONObject.from(op).getString("insert").length();
            if (length > 20000) return false;
        }
        return true;
    }
}

package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Topic;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.mapper.AccountMapper;
import com.example.mapper.TopicMapper;
import com.example.mapper.TopicTypeMapper;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    TopicTypeMapper mapper;

    @Resource
    AccountMapper accountMapper;

    @Resource
    FlowUtils flowUtils;

    @Resource
    CacheUtils cacheUtils;

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

    /**
     * 创建帖子
     * @param uid 帖子发起人
     * @param vo 创建帖子的对象
     * @return 返回错误信息或null
     */
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
        topic.setUsername(accountMapper.selectById(uid).getUsername());
        topic.setAvatar(accountMapper.selectById(uid).getAvatar());
        if (this.save(topic)) {
            cacheUtils.deleteCache(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            // 帖子发表后, 需要更新帖子预览表, 需要删除缓存中之前的的帖子预览列表, 重新请求
            return null;
        }else {
            return "内部错误, 请联系管理员!";
        }
    }

    /**
     * 获取帖子预览列表
     * @param page 当前页
     * @param type 类型
     * @return 放回帖子预览列表
     */
    @Override
    public List<TopicPreviewVO> listTopicByPage(int page, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + page + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFromCache(key, TopicPreviewVO.class);
        if (list != null) return list;
        List<Topic> topics;
        if (type == 0){
            topics = baseMapper.topicList(page * 10);
        }else {
            topics = baseMapper.topicLisByType(page * 10, type);
        }
        if (topics.isEmpty()) return null;
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }

    /**
     * 将Topic转换为TopicPreview
     * @param topic
     * @return
     */
    private TopicPreviewVO resolveToPreview(Topic topic){
        TopicPreviewVO vo = new TopicPreviewVO();
        BeanUtils.copyProperties(topic, vo);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        for (Object op : ops){
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text){
                if (previewText.length() >= 300) break;
                previewText.append(text);
            } else if (insert instanceof Map<?,?> map) {
                Optional.ofNullable(map.get("image"))
                        .ifPresent(obj -> images.add(obj.toString()));
            }
        }
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) + "..." : previewText.toString());
        vo.setImage(images);
        return vo;
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

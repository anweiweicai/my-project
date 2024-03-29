package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVO;
import com.example.entity.vo.request.TopicCreateVO;
import com.example.entity.vo.request.TopicUpdateVO;
import com.example.entity.vo.response.CommentVO;
import com.example.entity.vo.response.TopicDetailVO;
import com.example.entity.vo.response.TopicPreviewVO;
import com.example.entity.vo.response.TopicTopVO;
import com.example.mapper.*;
import com.example.service.NotificationService;
import com.example.service.TopicService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
public class TopicServiceImpl extends ServiceImpl<TopicMapper, Topic> implements TopicService {
    @Resource
    TopicTypeMapper mapper;

    @Resource
    AccountMapper accountMapper;

    @Resource
    AccountDetailsMapper accountDetailsMapper;

    @Resource
    AccountPrivacyMapper accountPrivacyMapper;

    @Resource
    TopicCommentMapper commentMapper;

    @Resource
    FlowUtils flowUtils;

    @Resource
    CacheUtils cacheUtils;

    @Resource
    StringRedisTemplate template;

    @Resource
    NotificationService notificationService;

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
        if (!textLimitCheck(vo.getContent(), 20000)) return "文章内容超过限定字数, 请重新编写!";
        if (!types.contains(vo.getType())) return "文章类型非法!";
        if (!flowUtils.limitPeriodCounterCheck(key, 5, 3600)) return "您在一小时内发文超过5篇, 过于频繁, 请稍后再试!";
        Topic topic = new Topic();
        BeanUtils.copyProperties(vo, topic);
        topic.setContent(vo.getContent().toJSONString());
        topic.setUid(uid);
        topic.setTime(new Date());
        if (this.save(topic)) {
            cacheUtils.deleteCachePattern(Const.FORUM_TOPIC_PREVIEW_CACHE + "*");
            // 帖子发表后, 需要更新帖子预览表, 需要删除缓存中之前的的帖子预览列表, 重新请求
            return null;
        }else {
            return "内部错误, 请联系管理员!";
        }
    }

    /**
     * 更新帖子
     * @param uid
     * @param vo
     * @return
     */
    @Override
    public String updateTopic(int uid, TopicUpdateVO vo) {
        if (!textLimitCheck(vo.getContent(), 20000)) return "文章内容超过限定字数, 请重新编写!";
        if (!types.contains(vo.getType())) return "文章类型非法!";
        baseMapper.update(null, Wrappers.<Topic>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toString())
                .set("type", vo.getType())
        );
        return null;
    }

    /**
     * 发表评论
     * @param uid
     * @param vo
     * @return
     */
    @Override
    public String createComment(int uid, AddCommentVO vo) {
        String key = Const.FORUM_TOPIC_COMMENT_COUNTER + uid;
        if (!flowUtils.limitPeriodCounterCheck(key, 5, 60)) return "您在1分钟内发表评论过于频繁, 请稍后再试!";
        if (!textLimitCheck(JSONObject.parseObject(vo.getContent()),2000)) return "评论内容超过限定字数, 请重新编写!";
        TopicComment comment = new TopicComment();
        comment.setUid(uid);
        BeanUtils.copyProperties(vo, comment);
        comment.setTime(new Date());
        commentMapper.insert(comment);
        Topic topic = baseMapper.selectById(vo.getTid());
        Account account = accountMapper.selectById(uid);
        if (vo.getQuote() > 0){
            TopicComment com = commentMapper.selectById(vo.getQuote());
            if (!Objects.equals(account.getId(), com.getUid())){
                notificationService.addNotification(
                        com.getUid(),
                        "您有新的评论回复",
                        account.getUsername() + " 回复了你的评论, 快去看看吧! ",
                        "success","/index/topic-detail/" + com.getTid()
                );
            }
        } else if (!Objects.equals(account.getId(), topic.getUid())) {
            notificationService.addNotification(
                topic.getUid(),
                    "您有新的帖子回复",
                    account.getUsername()+"回复了你发表的帖子: " + topic.getTitle() + ", 快去看看吧! ",
                    "success", "/index/topic-detail/" + vo.getTid()
            );
        }
        return null;
    }

    /**
     * 获取帖子评论
     * @param tid
     * @param pageNumber
     * @return
     */
    @Override
    public List<CommentVO> comments(int tid, int pageNumber) {
        Page<TopicComment> page = Page.of(pageNumber, 10);
        commentMapper.selectPage(page, Wrappers.<TopicComment>query().eq("tid", tid));

        return page.getRecords().stream().map(dto -> {
            CommentVO vo = new CommentVO();
            BeanUtils.copyProperties(dto, vo);
            if (dto.getQuote() > 0){
                TopicComment comment = commentMapper.selectOne(Wrappers.<TopicComment>query()
                        .eq("id", dto.getQuote())
                        .orderByAsc("time"));
                if (comment != null) {
                    JSONObject object = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"), builder, ignore -> {});
                    vo.setQuote(builder.toString());
                }else {
                    vo.setQuote("该评论已被删除");
                }
            }
            CommentVO.User user = new CommentVO.User();
            this.fillUserDetailsByPrivacy(user, dto.getUid());
            vo.setUser(user);
            return vo;
        }).toList();
    }

    /**
     * 删除评论
     * @param id 评论id
     * @param uid 评论人的id
     */
    @Override
    public void deleteComment(int id, int uid) {
        commentMapper.delete(Wrappers.<TopicComment>query()
                .eq("id", id)
                .eq("uid", uid));
    }

    /**
     * 获取帖子预览列表
     * @param pageNumber 当前页
     * @param type 类型
     * @return 放回帖子预览列表
     */
    @Override
    public List<TopicPreviewVO> listTopicByPage(int pageNumber, int type) {
        String key = Const.FORUM_TOPIC_PREVIEW_CACHE + pageNumber + ":" + type;
        List<TopicPreviewVO> list = cacheUtils.takeListFromCache(key, TopicPreviewVO.class);
        if (list != null) return list;
        // 从数据库中获取 帖子预览列表
        Page<Topic> page = Page.of(pageNumber, 10);
        if (type == 0){
            // 通过mybatisplus的分页插件获取分页数, 从而实现帖子预览
            baseMapper.selectPage(page, Wrappers.<Topic>query().orderByDesc("time"));
        }else {
            // 通过mybatisplus的分页插件获取分页数, 从而实现帖子预览
             baseMapper.selectPage(page, Wrappers.<Topic>query().eq("type", type).orderByDesc("time"));
        }
        // 将分页结果转换为帖子预览
        List<Topic> topics = page.getRecords();
        if (topics.isEmpty()) return null;
        list = topics.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }

    @Override
    public List<TopicTopVO> listTopicTop() {
        List<Topic> topics = baseMapper.selectList(Wrappers.<Topic>query()
                .select("id", "title", "time")
                .eq("top", 1));
        return topics.stream().map(topic -> {
            TopicTopVO vo = new TopicTopVO();
            BeanUtils.copyProperties(topic, vo);
            return vo;
        }).toList();
    }

    @Override
    public TopicDetailVO getTopic(int tid, int uid) {
        TopicDetailVO vo = new TopicDetailVO();
        Topic topic = baseMapper.selectById(tid);
        BeanUtils.copyProperties(topic, vo);
        TopicDetailVO.Interact interact = new TopicDetailVO.Interact(
                hasInteract(tid, uid, "like"),
                hasInteract(tid, uid, "collect")
        );
        vo.setInteract(interact);
        TopicDetailVO.User user = new TopicDetailVO.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, topic.getUid()));
        vo.setComments(commentMapper.selectCount(Wrappers.<TopicComment>query().eq("tid", tid)));
        return vo;
    }

    @Override
    public void interact(Interact interact, boolean state) {
        String type = interact.getType();
        synchronized (type.intern()){
            template.opsForHash().put(type, interact.toKey(), Boolean.toString(state));
            this.saveInteractSchedule(type);
        }
    }

    /**
     * 获取收藏的帖子
     * @param uid
     * @return
     */
    @Override
    public List<TopicPreviewVO> listTopicCollects(int uid) {
        return baseMapper.collectTopics(uid)
                .stream()
                .map(topic -> {
                    TopicPreviewVO vo = new TopicPreviewVO();
                    BeanUtils.copyProperties(topic, vo);
                    return vo;
                })
                .toList();
    }

    /**
     * 判断是否有点赞和收藏
     * @param tid
     * @param uid
     * @param type
     * @return
     */
    private boolean hasInteract(int tid, int uid, String type){
        String key = tid + ":" + uid;
        if (template.opsForHash().hasKey(type, key)){
            return Boolean.parseBoolean(template.opsForHash().entries(type).get(key).toString());
        }
        return baseMapper.userInteractCount(tid, uid, type) > 0;
    }

    /**
     * 用于定时同步缓存中的记录
     */
    private final Map<String, Boolean> state = new HashMap<>();
    /**
     * 创建定时任务功能实例
     */
    ScheduledExecutorService service = Executors.newScheduledThreadPool(2);

    /**
     * 创建定时任务
     * @param type
     */
    private void saveInteractSchedule(String type){
        if (!state.getOrDefault(type, false)) {
            state.put(type, true);
            service.schedule(() -> {
                this.saveInteract(type);
                state.put(type, false);
            }, 3, TimeUnit.SECONDS);
        }
    }

    /**
     * 将缓存中的记录分类并同步到数据库中
     * @param type
     */
    private void saveInteract(String type){
        synchronized (type.intern()){
            List<Interact> check = new LinkedList<>();
            List<Interact> uncheck = new LinkedList<>();
            template.opsForHash().entries(type).forEach((k, v) -> {
                if (Boolean.parseBoolean(v.toString())){
                    check.add(Interact.parseInteract(k.toString(), type));
                }else {
                    uncheck.add(Interact.parseInteract(k.toString(), type));
                }
            });
            if (!check.isEmpty()){
                baseMapper.addInteract(check, type);
            }
            if (!uncheck.isEmpty()){
                baseMapper.deleteInteract(uncheck, type);
            }
            template.delete(type);
        }
    }
    /**
     * 将用户详情拷贝到目标对象中
     * @param target 目标对象
     * @param uid 用户ID
     * @return
     */
    private <T> T fillUserDetailsByPrivacy(T target, int uid){
        AccountDetails details = accountDetailsMapper.selectById(uid);
        Account account = accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        // 获取用户隐私设置中设置了不公开的隐私信息
        String[] ignores = accountPrivacy.hiddenFields();
        // 拷贝用户信息到目标对象中并且不将 ignores中的字段拷贝
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(details, target, ignores);
        return target;
    }

    /**
     * 将Topic转换为TopicPreview
     * @param topic
     * @return
     */
    private TopicPreviewVO resolveToPreview(Topic topic){
        TopicPreviewVO vo = new TopicPreviewVO();
        // 获取帖子发起人信息
        BeanUtils.copyProperties(accountMapper.selectById(topic.getUid()), vo);
        BeanUtils.copyProperties(topic, vo);
        vo.setLike(baseMapper.interactCount(topic.getId(), "like"));
        vo.setCollect(baseMapper.interactCount(topic.getId(), "collect"));
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(topic.getContent()).getJSONArray("ops");
        this.shortContent(ops, previewText, obj -> images.add(obj.toString()));
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) + "..." : previewText.toString());
        vo.setImage(images);
        return vo;
    }

    /**
     * 截取文本
     * @param ops
     * @param previewText
     * @param imageHandler
     */
    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler){
        for (Object op : ops){
            Object insert = JSONObject.from(op).get("insert");
            if (insert instanceof String text){
                if (previewText.length() >= 300) break;
                previewText.append(text);
            } else if (insert instanceof Map<?,?> map) {
                Optional.ofNullable(map.get("image"))
                        .ifPresent(imageHandler);
            }
        }
    }

    /**
     * 检查文本长度是否超过限定字数 20000字
     * @param object
     * @return 超过返回false, 未超过返回true
     */
    private boolean textLimitCheck(JSONObject object, int max) {
        if (object == null) return false;
        long length = 0;
        for (Object op : object.getJSONArray("ops")){
            length += JSONObject.from(op).getString("insert").length();
            if (length > max) return false;
        }
        return true;
    }
}

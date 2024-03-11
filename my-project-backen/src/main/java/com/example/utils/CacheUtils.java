package com.example.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class CacheUtils {
    @Resource
    StringRedisTemplate template;

    /**
     * 从缓存中取数据
     * @param key
     * @param dataType
     * @return
     * @param <T>
     */
    public <T> T takeFromCache(String key, Class<T> dataType) {
        String s = template.opsForValue().get(key);
        if (s == null) return null;
        return JSONObject.parseObject(s).to(dataType);
    }

    /**
     * 从缓存中取数据list
     * @param key
     * @param itemType
     * @return
     * @param <T>
     */
    public <T> List<T> takeListFromCache(String key, Class<T> itemType) {
        String s = template.opsForValue().get(key);
        if (s == null) return null;
        return JSONArray.parseArray(s).toList(itemType);
    }

    /**
     * 保存数据到缓存
     * @param key
     * @param data
     * @param expire
     * @param <T>
     */
    public <T> void saveToCache(String key, T data, long expire) {
        template.opsForValue().set(key, JSONObject.from(data).toJSONString(), expire, TimeUnit.SECONDS);
    }

    /**
     * 保存数据list到缓存
     * @param key
     * @param list
     * @param expire
     * @param <T>
     */
    public <T> void saveListToCache(String key, List<T> list, long expire) {
        template.opsForValue().set(key, JSONArray.from(list).toJSONString(), expire, TimeUnit.SECONDS);
    }

    /**
     * 删除对应key的缓存
     * @param key
     */
    public void deleteCache(String key) {
        template.delete(key);
    }
}

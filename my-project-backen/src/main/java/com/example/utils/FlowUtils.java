package com.example.utils;

import ch.qos.logback.core.util.TimeUtil;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class FlowUtils {//限流

    @Resource
    StringRedisTemplate template;

    public boolean limitOnceCheck(String key, int blocktime){//键值, 冷却时间(在这个时间内不能重复执行操作)
        if (Boolean.TRUE.equals(template.hasKey(key))){//如果有这个key, 则返回false, 不能执行操作
            return false;
        }else {
            template.opsForValue().set(key, "", blocktime, TimeUnit.SECONDS);
            return true;
        }
    }
}

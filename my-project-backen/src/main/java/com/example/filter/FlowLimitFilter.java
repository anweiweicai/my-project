package com.example.filter;

import com.example.entity.RestBean;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
@Order(Const.ORDER_LIMIT)
public class FlowLimitFilter extends HttpFilter {

    @Resource
    StringRedisTemplate template;

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        String ip = request.getRemoteAddr();
        if (this.tryCount(ip)){
            chain.doFilter(request, response);
        } else {
            this.writeBlockMessage(response);
        }
    }
    private void writeBlockMessage(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);//访问被拒
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(RestBean.forbidden("操作频繁, 请稍后再试").asJsonString());
    }

    private boolean tryCount(String ip){// 对某一ip地址请求计数, 是否同意请求, true为同意, false为不同意
        synchronized (ip.intern()){
            if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_BLOCK + ip))) return false;
            return this.limitPeriodCheck(ip);
        }
    }

    private boolean limitPeriodCheck(String ip){//请求频率限制
        if (Boolean.TRUE.equals(template.hasKey(Const.FLOW_LIMIT_COUNTER + ip))) {
            Long increment =  Optional.ofNullable(template.opsForValue().increment(Const.FLOW_LIMIT_COUNTER + ip)).orElse(0L);
            // 使用了Optional类来处理可能为null的情况。首先尝试从缓存中获取以用户IP地址为key的计数器的值，然后对其进行自增操作。
            // 如果获取到的值为null，则将其替换为0，然后再进行自增操作。最终返回自增后的计数器的值
            if (increment > 10){
                template.opsForValue().set(Const.FLOW_LIMIT_BLOCK + ip, "", 30, TimeUnit.SECONDS);
                return false;
            }
        } else {
            template.opsForValue().set(Const.FLOW_LIMIT_COUNTER + ip, "1", 3, TimeUnit.SECONDS);
        }
        return true;
    }
}

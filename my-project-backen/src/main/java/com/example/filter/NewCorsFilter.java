package com.example.filter;

import com.example.utils.Const;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component//该类继承了HttpFilter, 而HttpFilter实现了`javax.servlet.Filter`接口, 所以会被识别为过滤器
@Order(Const.ORDER_CORS)//跨域请求设置高优先级
public class NewCorsFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {
        this.addCorsHeader(request, response);
        chain.doFilter(request, response);
    }

    private void addCorsHeader(HttpServletRequest request,
                               HttpServletResponse response){
        response.addHeader("Access-Control-Allow-Origin",request.getHeader("Origin"));//添加允许的源站点
        response.addHeader("Access-Control-Allow-Methods","GET, POST, PUT, DELETE, OPTIONS");//添加允许的方法
        response.addHeader("Access-Control-Allow-Headers","Authorization, Content-Type");
    }
}

package com.example.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.utils.Const;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthorizeFileter extends OncePerRequestFilter {//每次请求都执行过滤器
    //自定义验证逻辑
    @Resource
    JwtUtils utils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        DecodedJWT jwt = utils.resolveJwt(authorization);
        if (jwt != null){
            UserDetails user = utils.toUser(jwt);
            // 创建一个基于用户名密码的身份验证对象
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            // 将关于认证请求的更多细节添加到`authentication`对象中，这些细节包括了请求的IP地址、会话ID等
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            // 将已经创建并填充了用户身份验证信息的身份验证对象，设置为当前线程的安全上下文的认证对象
            // 在后续的请求处理中，可以通过安全上下文获取到用户的身份验证信息，从而进行相应的授权和访问控制处理。
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 获取当前用户名
            request.setAttribute(Const.ATTR_USER_ID,utils.toId(jwt));
        }

        filterChain.doFilter(request, response);
        // 表示继续调用过滤器链中的下一个过滤器，或者如果没有下一个过滤器，则调用servlet或者JSP页面
        // 实现过滤器链中的请求处理传递，确保请求能够顺利通过所有的过滤器进行处理
    }
}

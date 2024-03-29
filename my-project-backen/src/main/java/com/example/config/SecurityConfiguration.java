package com.example.config;

import com.example.entity.RestBean;
import com.example.entity.dto.Account;
import com.example.entity.vo.response.AuthorizeVO;
import com.example.filter.JwtAuthorizeFileter;
import com.example.service.AccountService;
import com.example.utils.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {

    @Resource
    JwtUtils utils;

    @Resource
    JwtAuthorizeFileter jwtAuthorizeFileter;

    @Resource
    AccountService service;

    @Bean//过滤器链
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                //访问限制配置
                //传入conf,类型为AuthorizationManagerRequestMatcherRegistry, 执行conf的相关方法
                .authorizeHttpRequests(conf -> conf
                        .requestMatchers("/api/auth/**", "/error", "/images/**").permitAll()//表示配置对特定请求的放行. 允许api里面的页面请求(在用户未验证之前可以访问的界面)
                        .anyRequest().authenticated()//表示对除这些特定请求之外的所有请求进行身份验证
                )
                //登录验证配置
                .formLogin(conf -> conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::onAuthenticationFailure)//登录失败, 自带验证逻辑
                        .successHandler(this::onAuthenticationSuccess)//登录成功, 自带验证逻辑
                )
                //登出配置
                .logout(conf -> conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)//登出成功
                )
                .exceptionHandling(conf -> conf
                        .authenticationEntryPoint(this::onUnauthorized)//处理认证（authentication）异常,当用户尝试访问受保护的资源但未经身份验证时，将触发认证异常
                        .accessDeniedHandler(this::onAccessDeny)//处理访问拒绝（access denied）异常,当认证通过但用户没有足够的权限来访问资源时，将触发访问拒绝异常
                )
                //csrf是防止跨站请求伪造(会为本网站用户请求提供令牌, 以保证不是其它网站的请求), 这里配置是禁用CSRF保护功能
                .csrf(AbstractHttpConfigurer::disable)
                //sessionManagement会话管理 用于控制和配置用户的会话行为, 这里是用JWT处理session, 这里不需要处理session, 将其设置为无状态
                .sessionManagement(conf -> conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                //加入jwt验证
                .addFilterBefore(jwtAuthorizeFileter, UsernamePasswordAuthenticationFilter.class)
                .build();//构建
    }

    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        response.setContentType("application/json");//告诉前端 后端返回的是json格式
        response.setCharacterEncoding("UTF-8");//字符编码格式为UTF-8
        User user = (User) authentication.getPrincipal();//获取用户详细信息, 通过验证后会将用户的完整信息给Authentication
        Account account = service.findAccountByNameOrEmail(user.getUsername());
        String token = utils.creatJwt(user,account.getId(),account.getUsername());
        AuthorizeVO vo = new AuthorizeVO();
        BeanUtils.copyProperties(account, vo);//将account的属性复制到vo对应的属性中, 如account的role的值赋值给了vo的role
        vo.setExpire(utils.expireTime());//发送过期时间
//        vo.setRole(account.getRole());//发送角色名
        vo.setToken(token);//发送token
//        vo.setUsername(account.getUsername());//发送用户名

//        另一种实现方式, 将account的属性复制到vo对应的属性中:
//        AuthorizeVO vo = account.asViewObject(AuthorizeVO.class, v -> {
//            v.setExpire(utils.expireTime());//发送过期时间
//            v.setToken(token);//发送token
//        });
        response.getWriter().write(RestBean.success(vo).asJsonString());//将返回值变为json格式
    }

    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {
        response.setContentType("application/json");//告诉前端 后端返回的是json格式
        response.setCharacterEncoding("UTF-8");//字符编码格式为UTF-8
        PrintWriter writer = response.getWriter();
        String authorization = request.getHeader("Authorization");
        if (utils.invalidateJwt(authorization)) {
            writer.write(RestBean.success().asJsonString());
        }else {
            writer.write(RestBean.failure(400,"退出登录失败").asJsonString());
        }
    }

    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {
        response.setContentType("application/json");//告诉前端 后端返回的是json格式
        response.setCharacterEncoding("UTF-8");//字符编码格式为UTF-8
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());//获取错误信息 并返回给前端
    }

    public void onAccessDeny(HttpServletRequest request,
                             HttpServletResponse response,
                             AccessDeniedException exception) throws IOException {
        response.getWriter().write(RestBean.forbidden(exception.getMessage()).asJsonString());
    }
    public void onUnauthorized(HttpServletRequest request,
                               HttpServletResponse response,
                               AuthenticationException exception) throws IOException {
        response.getWriter().write(RestBean.unauthorized(exception.getMessage()).asJsonString());
    }
}

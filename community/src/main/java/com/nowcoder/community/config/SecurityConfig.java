package com.nowcoder.community.config;

import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {

    //重写三个configure

    @Override  //第一个configure 带websecurity参数的  用来忽略掉所有的静态资源
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override  //第二个configure 带httpSecurity参数的  进行授权
    protected void configure(HttpSecurity http) throws Exception {
        //授权
        http.authorizeRequests()
                .antMatchers(  //路径中的请求需要有下方定义的权限才可以访问
                        //路径
                        "/user/setting",
                        "/user/upload",
                        "/discuss/add",
                        "/comment/add/**",
                        "/message",
                        "/message/detail/**",
                        "/send/message",
                        "/notice",
                        "/notice/detail",
                        "/like",
                        "/follow"
                ).hasAnyAuthority(  //权限
                AUTHORITY_USER,AUTHORITY_ADMIN,AUTHORITY_MODERATOR
                )
                .antMatchers("/discuss/top/**","/discuss/elite/**")
                .hasAnyAuthority(
                        AUTHORITY_MODERATOR,AUTHORITY_ADMIN
                )
                .antMatchers("/data")
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .antMatchers("/discuss/delete/**")
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest().permitAll()//其余的请求全部放行
                .and().csrf().disable();//不启用csrf攻击授权

        //权限不够时的处理
        http.exceptionHandling()
                .authenticationEntryPoint(new AuthenticationEntryPoint() {//未认证时的处理
                    @Override
                    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
                        String xRequestedWith =  request.getHeader("X-Requested-With");//判断请求是异步还是同步
                        if(xRequestedWith!=null&&xRequestedWith.equals("XMLHttpRequest"))
                        {
                            //异步请求  发送json字符串
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403,"未登录",null));
                        }
                        else {
                            response.sendRedirect(request.getContextPath()+"/login");
                        }
                    }
                })
                .accessDeniedHandler(new AccessDeniedHandler() { //权限不足时的处理
                    @Override
                    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException e) throws IOException, ServletException {
                        String xRequestedWith =  request.getHeader("x-requested-with");//判断请求是异步还是同步
                        if(xRequestedWith!=null&&xRequestedWith.equals("XMLHttpRequest"))
                        {
                            //异步请求  发送json字符串
                            response.setContentType("application/plain;charset=utf-8");
                            PrintWriter writer = response.getWriter();
                            writer.write(CommunityUtil.getJsonString(403,"权限不足",null));
                        }
                        else {
                            response.sendRedirect(request.getContextPath()+"/denied");
                        }
                    }
                });

        //security底层默认拦截/logout路径
        //需要覆盖掉，才能使用自己的退出逻辑
        http.logout().logoutUrl("/securitylogout");//设置一个不存在的路径为拦截路径



    }
}

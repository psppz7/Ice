package com.nowcoder.community.config;

import com.nowcoder.community.controller.intercepter.LoginInfoIntercepter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  //配置拦截器
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInfoIntercepter loginInfoIntercepter;

    @Override   //加入拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInfoIntercepter).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg");
    }
}

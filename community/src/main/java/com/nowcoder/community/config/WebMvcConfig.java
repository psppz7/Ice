package com.nowcoder.community.config;

import com.nowcoder.community.controller.intercepter.LoginInfoInterceptor;
import com.nowcoder.community.controller.intercepter.LoginRequiredInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration  //配置拦截器
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    LoginInfoInterceptor loginInfoIntercepter;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;

    @Override   //加入拦截器
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInfoIntercepter).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg");
        registry.addInterceptor(loginRequiredInterceptor).excludePathPatterns("/**/*.css","/**/*.js","/**/*.png","/**/*.jpg");
        //先注册的先执行
    }
}

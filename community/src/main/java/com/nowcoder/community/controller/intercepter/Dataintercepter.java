package com.nowcoder.community.controller.intercepter;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DataService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@Component
public class Dataintercepter implements HandlerInterceptor {

    @Autowired
    DataService dataService;

    @Autowired
    HostHolder hostHolder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //统计uv
        String ip = request.getRemoteHost();
        dataService.recordUv(ip);
        //统计dau
        User user = hostHolder.getUser();
        if(user!=null)
        {
            dataService.recordDau(user.getId());
        }
        return true;
    }
}

package com.nowcoder.community.controller.intercepter;

import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CookieUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class LoginInfoInterceptor implements HandlerInterceptor {
    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;
    //在controller之前拦截
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String ticket = CookieUtil.getValue(request,"ticket");
         if(ticket!=null)
         {
             LoginTicket loginTicket = userService.findLoginTicket(ticket);
             //判断该loginticket是否有效
             if(loginTicket!=null&&loginTicket.getStatus()==0&&loginTicket.getExpired().after(new Date()))
             {
                 User user = userService.findUserById(loginTicket.getUserId());
                 //在本次请求中持有用户
                 hostHolder.setUser(user);
             }
         }
        return true;
    }
    //在模板之前
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        User user = hostHolder.getUser();
        if(user!=null&&modelAndView!=null)
        {
            modelAndView.addObject("loginUser",user);
        }
    }
    //模板之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        hostHolder.clean();
    }
}

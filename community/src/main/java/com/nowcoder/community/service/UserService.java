package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    UserMapper userMapper;  //数据库mapper注入

    @Autowired
    TemplateEngine templateEngine; //模板引擎注入

    @Autowired
    MailClient mailClient; //发送邮件工具注入

    @Autowired
    LoginTicketMapper loginTicketMapper;//登录mapper注入

    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    public User findUserById(int id)
    {
        return userMapper.selectById(id);
    }

    public Map<String,String> register(User user)  //注册业务
    {
        Map<String,String> map = new HashMap<>();
        //验证账号是否被注册
        if(userMapper.selectByName(user.getUsername())!=null)
        {
            map.put("usernameMsg","该用户名已被注册");
            return map;
        }
        //验证邮箱是否被注册
        if(userMapper.selectByEmail(user.getEmail())!=null)
        {
            map.put("emailMsg","该邮箱已被注册");
            return map;
        }
        //设置user的属性
        user.setSalt(CommunityUtil.generateUUID().substring(0,5));
        user.setPassword(CommunityUtil.md5(user.getPassword()+user.getSalt())); //密码加密

        user.setType(0);
        user.setStatus(0); //未激活
        user.setActivationCode(CommunityUtil.generateUUID());                   //生成随机头像
        user.setHeaderUrl(String.format("https://images.nowcoder.com/head/%dt.png",new Random().nextInt(1000)));
        user.setCreateTime(new Date());
        //插入数据库
        userMapper.insertUser(user);

        //发激活邮件
        Context context = new Context();
        context.setVariable("email",user.getEmail());

        String url = domain + contextPath + "/activation/" + user.getId() + "/" + user.getActivationCode();
        context.setVariable("url",url);

        String content = templateEngine.process("/mail/activation",context);
        mailClient.sendMail(user.getEmail(),"账号激活",content);

        return map;
    }
    public int activation(int userId,String activationCode)  //激活业务
    {
        User user = userMapper.selectById(userId);
        if(user.getStatus()==1)
        {
            return ACTIVATION_REAPEAT;
        }
        else if(user.getActivationCode().equals(activationCode))
        {
            userMapper.updateStatus(userId,1);
            return ACTIVATION_SUCCESS;
        }
        else
        {
            return ACTIVATION_FAILURE;
        }
    }
    public Map<String,String> login(String username,String password,int expired)
    {
        Map<String,String> map = new HashMap<>();
        //验证账号
        User user = userMapper.selectByName(username);
        if(user==null)                                //账号不存在
        {
            map.put("usernameMsg","账号不存在");
            return map;
        }
        if(user.getStatus()==0)
        {
            map.put("usernameMsg","账号未激活");
            return map;
        }
        if(!user.getPassword().equals(CommunityUtil.md5(password+user.getSalt()))) //密码错误
        {
            map.put("passwordMsg","密码错误");
            return map;
        }
        else                           //密码正确登录成功
        {
            //生成登录凭证
            LoginTicket loginTicket = new LoginTicket();
            loginTicket.setUserId(user.getId());
            loginTicket.setTicket(CommunityUtil.generateUUID());
            loginTicket.setStatus(0);
            loginTicket.setExpired(new Date(System.currentTimeMillis()+expired*1000));
            loginTicketMapper.insertLoginTicket(loginTicket);
            map.put("ticket",loginTicket.getTicket());
        }
        return map;
    }
    public void logout(String ticket)//退出登录
    {
        loginTicketMapper.updateStatus(ticket,1);
    }
    public LoginTicket findLoginTicket(String ticket)
    {
        return loginTicketMapper.selectLoginTicket(ticket);
    }
}

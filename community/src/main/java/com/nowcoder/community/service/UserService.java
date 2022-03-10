package com.nowcoder.community.service;

import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.MailClient;
import com.nowcoder.community.util.RedisKeyUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class UserService implements CommunityConstant {
    @Autowired
    UserMapper userMapper;  //数据库mapper注入

    @Autowired
    TemplateEngine templateEngine; //模板引擎注入

    @Autowired
    MailClient mailClient; //发送邮件工具注入

    @Autowired
    RedisTemplate redisTemplate;

/*    @Autowired
    LoginTicketMapper loginTicketMapper;//登录mapper注入*/
    @Value("${community.path.domain}")
    String domain;

    @Value("${server.servlet.context-path}")
    String contextPath;

    public User findUserById(int id)
    {
      //  return userMapper.selectById(id);
        User user = getCache(id);
        if(user==null)
            user = initCache(id);
        return user;
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
        cleanCache(userId);
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
         //   loginTicketMapper.insertLoginTicket(loginTicket);
            String redisKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
            redisTemplate.opsForValue().set(redisKey,loginTicket);


            map.put("ticket",loginTicket.getTicket());
        }
        return map;
    }
    public void logout(String ticket)//退出登录
    {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(redisKey);
        loginTicket.setStatus(1);
        redisTemplate.opsForValue().set(redisKey,loginTicket);
    }
    public LoginTicket findLoginTicket(String ticket)
    {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
        return (LoginTicket) redisTemplate.opsForValue().get(redisKey);
    }

    public int updateHeader(int id,String url)
    {
        int rows = userMapper.updateHeader(id,url);
        cleanCache(id);
       return rows;
    }

    public int updatePassword(String ticket,int userId,String password)
    {
        String redisKey = RedisKeyUtil.getTicketKey(ticket);
      LoginTicket loginTicket = (LoginTicket)redisTemplate.opsForValue().get(redisKey);
      loginTicket.setStatus(1);
      int rows = userMapper.updatePassword(userId,password);
      redisTemplate.opsForValue().set(redisKey,loginTicket);
      cleanCache(userId);
        return rows;
    }

    public void forgetPassword(String email,String newPassword)
    {
        Map<String,String> map = new HashMap<>();
       User user = userMapper.selectByEmail(email);
       newPassword = CommunityUtil.md5(newPassword+user.getSalt());
       userMapper.updatePassword(user.getId(),newPassword);
       cleanCache(user.getId());
    }
    public Map<String,String> getForgetKaptcha(String email,String code)
    {
        Map<String,String> map = new HashMap<>();
        User user = userMapper.selectByEmail(email);
        if(user==null)
        {
            map.put("emailMsg","该账号不存在");
            return map;
        }

        mailClient.sendMail(email,"密码找回--验证码",code);
        map.put("emailMsg","验证码已发送");
        return map;
    }

    //1.优先从缓存中取值
    public User getCache(int userId)
    {
        String redisKey = RedisKeyUtil.getUsertKey(userId);
        User user = (User) redisTemplate.opsForValue().get(redisKey);
        return user;
    }
    //2.取不到时初始化缓存
    public User initCache(int userId)
    {
        User user = userMapper.selectById(userId);
        String redisKey = RedisKeyUtil.getUsertKey(userId);
        redisTemplate.opsForValue().set(redisKey,user,3600, TimeUnit.SECONDS);
        return user;
    }
    //3.数据变更时清除缓存
    public void cleanCache(int userId)
    {
        String redisKey = RedisKeyUtil.getUsertKey(userId);
        redisTemplate.delete(redisKey);
    }

    public Collection<? extends GrantedAuthority> getAuthorities(int userId)
    {
        User user = findUserById(userId);

        List<GrantedAuthority>  list = new ArrayList<>();

        list.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                switch (user.getType())
                {
                    case 1:
                        return AUTHORITY_ADMIN;
                    case 2:
                        return AUTHORITY_MODERATOR;
                    default:
                        return AUTHORITY_USER;
                }
            }
        });
        return list;

    }
}

package com.nowcoder.community.controller;

import com.google.code.kaptcha.Producer;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    private UserService userService;//用户服务

    @Autowired
    private Producer kaptcha;//验证码服务


    @RequestMapping(path = "/register",method = RequestMethod.GET)  //网站名
    public String getRegisterPage(Model model)
    {
        model.addAttribute("user",null);
        return "/site/register";  //文件名
    }

    @RequestMapping(path = "/register",method = RequestMethod.POST)
    public String register(User user,Model model)
    {
        Map<String,String> map =userService.register(user);
        if(map==null||map.isEmpty())
        {
            model.addAttribute("msg","注册成功，已经向您发送激活邮件，请尽快激活");
            model.addAttribute("path","/index");
            return "/site/operate-result";
        }
        else
        {
            model.addAttribute("usernameMsg",map.getOrDefault("usernameMsg",null));
            model.addAttribute("emailMsg",map.getOrDefault("emailMsg",null));
            model.addAttribute("passwordMsg",map.getOrDefault("passwordMsg",null));
            return "/site/register";
        }
    }
    @RequestMapping(path = "/activation/{userId}/{activationCode}",method = RequestMethod.GET)
    public String activation(Model model,@PathVariable("userId") int userId,@PathVariable("activationCode") String activationCode)
    {
        int result = userService.activation(userId,activationCode);
        if(result==ACTIVATION_SUCCESS)
        {
            model.addAttribute("msg","激活成功");
            model.addAttribute("path","/login");
        }
        else if(result==ACTIVATION_REAPEAT)
        {
            model.addAttribute("msg","请勿重复激活");
            model.addAttribute("path","/index");
        }
        else if(result==ACTIVATION_FAILURE)
        {
            model.addAttribute("msg","激活失败");
            model.addAttribute("path","/index");
        }
        return "/site/operate-result";
    }

    @RequestMapping(path = "/login",method = RequestMethod.GET)
    public String getLoginPage()
    {
        return "/site/login";
    }


    @RequestMapping(path = "/kaptcha",method = RequestMethod.GET)
    public void getKaptcha(HttpServletResponse response, HttpSession session)  //验证码需要存到session之内
    {
        //获取验证码
        String str = kaptcha.createText();
        BufferedImage image = kaptcha.createImage(str);

        session.setAttribute("kaptchaStr",str);

        response.setContentType("image/png");//输出图片适合使用字节流

        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image,"png",os);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @RequestMapping(path = "/login",method = RequestMethod.POST)
    public String login(String username,String password,String code,boolean isremember,Model model,HttpSession session,HttpServletResponse response)
    {
        //检查验证码
        String kaptcha = String.valueOf(session.getAttribute("kaptchaStr"));
        if(!kaptcha.equalsIgnoreCase(code))//忽略大小写
        {
            model.addAttribute("codeMsg","验证码不正确");
            return "/site/login";
        }
        //检查账号密码
        //登录凭证有效时间
        int expiredSeconds = isremember? CommunityConstant.REMEMBER_EXPIRED:CommunityConstant.DEFAULT_EXPIRED;
        Map<String,String> map = userService.login(username,password,expiredSeconds);
        if(map.containsKey("ticket"))
        {
            Cookie cookie = new Cookie("ticket",map.get("ticket"));
            cookie.setPath("${server.servlet.context-path}");
            cookie.setMaxAge(expiredSeconds);
            response.addCookie(cookie);

            return "redirect:/index";
        }
        else
        {

            model.addAttribute("usernameMsg",map.get("usernameMsg"));
            model.addAttribute("passwordMsg",map.get("passwordMsg"));

            return "/site/login";
        }
    }
    @RequestMapping(path = "/logout")
    public String logout(@CookieValue("ticket") String ticket)
    {
        userService.logout(ticket);
        return "redirect:/index";
    }
}

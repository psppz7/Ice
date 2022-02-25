package com.nowcoder.community.controller;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {

    @Autowired
    UserService userService;

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
    public String login()
    {
        return "/site/login";
    }

}

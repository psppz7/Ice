package com.nowcoder.community.controller;

import com.nowcoder.community.service.AlphaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/alpha")
public class AlphaController {

    @RequestMapping("/hello")
    @ResponseBody
    public String sayHello()
    {
        return "hello springboot";
    }

    @Autowired
    private AlphaService alphaService;

    @RequestMapping("/check")
    @ResponseBody
    public String check()
    {
       return alphaService.find();
    }

    @RequestMapping("/get")
    public void get(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
    {
        System.out.println(httpServletRequest.getMethod());
        System.out.println(httpServletRequest.getParameter("name"));

        try {
            PrintWriter writer = httpServletResponse.getWriter();
            writer.write("<h1>123123<h1>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //获取参数  参数在？后面
    @RequestMapping("/gets")
    @ResponseBody
    public String gets(
            @RequestParam(name = "name",required = false,defaultValue = "张三") String name,
            @RequestParam(name = "age",required = false,defaultValue = "10") int age)
    {
        System.out.println(name);
        System.out.println(age);
        return "gets";
    }
    //获取参数 参数在路径中
    @RequestMapping("/getss/{id}")
    @ResponseBody
    public String getss(@PathVariable("id") int id)
    {
        System.out.println(id);
        return "getss";
    }
    //post请求
    @RequestMapping("/post")
    @ResponseBody
    public String post(String name,int age) //参数名称与表单对应name相同
    {
        System.out.println(name);
        System.out.println(age);
        return "post";
    }
    //使用Thymeleaf的动态html响应
    @RequestMapping("/response")
    public ModelAndView response()
    {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("name","张三");
        modelAndView.addObject("age",30);
        modelAndView.setViewName("html/teacher"); //默认html，只需要写文件名
        return modelAndView;
    }
    @RequestMapping("/responses")
    public String responses(Model model)
    {
        model.addAttribute("name","李四");
        model.addAttribute("age",45);
        return "html/teacher";
    }
    //传json数据
    @RequestMapping("/map")
    @ResponseBody
    public Map<String,String> map()
    {
        Map<String,String> map = new HashMap<>();
        map.put("张三","22");
        map.put("李四","33");
        return map;
    }

}

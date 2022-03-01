package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;

    @RequestMapping(path = "/index")
    public String showDiscussPosts(Model model, Page page)
    {
        //该方法会自动实例化model和page，并将page注入给model
        page.setRows(discussPostService.fineDiscussPostRows(0));
        page.setPath("/index");
        List<DiscussPost> list1 = discussPostService.findDiscussPosts(0,page.getOffset(),page.getLimit());
        List<Map<String,Object>> list2 = new ArrayList<>();

        for(DiscussPost d : list1)
        {
            User user = userService.findUserById(d.getUserId());
            Map<String,Object> map = new HashMap<>();
            map.put("post",d);
            map.put("user",user);
            list2.add(map);
        }

        model.addAttribute("page",page);
        model.addAttribute("discussPosts",list2);
        return "/index";
    }


}

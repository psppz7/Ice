package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController implements CommunityConstant {
    @Autowired
    DiscussPostService discussPostService;
    @Autowired
    UserService userService;
    @Autowired
    LikeService likeService;
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
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,d.getId());
            Map<String,Object> map = new HashMap<>();
            map.put("post",d);
            map.put("user",user);
            map.put("likeCount",likeCount);
            list2.add(map);
        }

        model.addAttribute("page",page);
        model.addAttribute("discussPosts",list2);
        return "/index";
    }

    @RequestMapping(path = "/denied")
    public String getDeniedPage()
    {
        return "/error/404";
    }


}

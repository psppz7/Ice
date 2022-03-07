package com.nowcoder.community.controller;

import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class SearchController implements CommunityConstant {

    @Autowired
    ElasticSearchService searchService;

    @Autowired
    UserService userService;

    @Autowired
    LikeService likeService;

    @RequestMapping(path = "/search",method = RequestMethod.GET)
    public String search(String keyword, Model model, Page page)
    {
        org.springframework.data.domain.Page<DiscussPost> searchResult =
             searchService.searchDiscussPost(keyword,page.getCurrent()-1,page.getLimit());

        List<Map<String,Object>> discussPosts = new ArrayList<>();

        for(DiscussPost post : searchResult)
        {
            Map<String,Object> map = new HashMap<>();
            User user = userService.findUserById(post.getUserId());
            long likeCount = likeService.findEntityLikeCount(ENTITY_TYPE_POST,post.getId());
            map.put("likeCount",likeCount);
            map.put("user",user);
            map.put("post",post);
            discussPosts.add(map);
        }
        model.addAttribute("discussPosts",discussPosts);
        model.addAttribute("keyword",keyword);

        page.setPath("/search?keyword="+ keyword);
        page.setRows(searchResult==null?0:(int) searchResult.getTotalElements());

        return "/site/search";
    }
}

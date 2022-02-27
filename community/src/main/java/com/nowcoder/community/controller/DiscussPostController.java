package com.nowcoder.community.controller;

import com.nowcoder.community.annotation.LoginRequired;
import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
@RequestMapping("/discuss")
public class DiscussPostController implements CommunityConstant{

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    @RequestMapping(path = "/add",method = RequestMethod.POST)
    @ResponseBody
    public String addDiscussPost(String title,String content)
    {
        User user = hostHolder.getUser();
        if(user==null)
        {
            return CommunityUtil.getJsonString(403,null,null);
        }
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(user.getId());
        discussPost.setTitle(title);
        discussPost.setContent(content);
        discussPost.setCreateTime(new Date());

        discussPostService.addDiscussPost(discussPost);

        return CommunityUtil.getJsonString(0,"发布成功",null);
    }

    @RequestMapping(path = "/detail/{id}",method = RequestMethod.GET)
    public String showDiscussDeatil(@PathVariable("id") int id, Model model, Page page)
    {
        Map<String,Object> map = discussPostService.showDiscussDetail(id);
        if(map.keySet()!=null) {
            model.addAttribute("userId",map.get("userId") );
            model.addAttribute("title", map.get("title"));
            model.addAttribute("content",map.get("content") );
            model.addAttribute("type", map.get("type"));
            model.addAttribute("status", map.get("status"));
            model.addAttribute("createTime", map.get("createTime"));
            model.addAttribute("commentCount", map.get("commentCount"));
            model.addAttribute("score",map.get("score") );
            model.addAttribute("headerUrl",map.get("headerUrl"));
            model.addAttribute("username",map.get("username"));
        }
        page.setLimit(5);
        page.setPath("/discuss/detail/" + id);
        page.setRows((Integer) map.get("commentCount"));


       List<Map<String,Object>> comInfo = new ArrayList<>();
        List<Map<String,Object>> replyInfo = new ArrayList<>();
                                                                       //评论类型：帖子
        List<Comment> commentList = commentService.findCommentByEntity(ENTITY_TYPE_POST,(Integer)map.get("id"),page.getOffset(),page.getLimit());
        for(Comment c: commentList)
        {
            Map<String,Object> tem1 = new HashMap<>();  //回复

            User user = userService.findUserById(c.getUserId());
            tem1.put("user",user);
            tem1.put("comment",c);
            List<Comment> replys = commentService.findCommentByEntity(ENTITY_TYPE_COMMENT,c.getId(),0,Integer.MAX_VALUE);
            tem1.put("replyCount",replys.size());
            comInfo.add(tem1);
            for(Comment r : replys)
            {
                Map<String,Object> tem2 = new HashMap<>();  //回复的回复
                if(r.getTargetId()!=0)
                {
                    User user2 = userService.findUserById(r.getTargetId());
                    tem2.put("target",user2);
                }
                else
                {
                    tem2.put("target",null);
                }
                User user1 = userService.findUserById(r.getUserId());
                tem2.put("user",user1);
                tem2.put("comment",r);

                replyInfo.add(tem2);
            }
        }
        model.addAttribute("comInfo",comInfo);
        model.addAttribute("replyInfo",replyInfo);
        return "/site/discuss-detail";
    }


}

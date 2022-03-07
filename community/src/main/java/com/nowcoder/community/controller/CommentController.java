package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.CommentService;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Date;

@Controller
@RequestMapping("/comment")
public class CommentController implements CommunityConstant {

    @Autowired
    CommentService commentService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    DiscussPostService discussPostService;

    @RequestMapping(path = "/add/{id}",method = RequestMethod.POST)
    public String addComment(@PathVariable("id") int discussPostId, Comment comment, Model model)
    {
        comment.setUserId(hostHolder.getUser().getId());
        comment.setStatus(0);
        comment.setCreateTime(new Date());

        commentService.addComment(comment);
        //触发评论事件
        Event event = new Event();
        event.setTopic(TOPIC_COMMENT);
        event.setEntityType(comment.getEntityType());
        event.setEntityId(comment.getEntityId());
        event.setUserId(hostHolder.getUser().getId());
        event.setData("discussPostId",discussPostId);
        if(comment.getEntityType()==ENTITY_TYPE_POST)
        {
           event.setEntityUserId(discussPostService.findDiscussPostsById(event.getEntityId()).getUserId());
        }
        else if(comment.getEntityType()==ENTITY_TYPE_COMMENT)
        {
            event.setEntityUserId(commentService.findCommentById(event.getEntityId()).getUserId());
        }
        //发送事件
        eventProducer.fireEvent(event);

        if(comment.getEntityType()==ENTITY_TYPE_POST)
        {
            //触发发帖事件
            event = new Event();
            event.setTopic(TOPIC_PUBLISH);
            event.setEntityType(comment.getEntityType());
            event.setEntityId(comment.getEntityId());
            event.setUserId(hostHolder.getUser().getId());

            eventProducer.fireEvent(event);
        }


        return "redirect:/discuss/detail/" + discussPostId;
    }

}

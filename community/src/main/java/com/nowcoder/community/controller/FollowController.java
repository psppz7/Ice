package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Event;
import com.nowcoder.community.event.EventProducer;
import com.nowcoder.community.service.FollowService;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class FollowController implements CommunityConstant {
    @Autowired
    FollowService followService;

    @Autowired
    EventProducer eventProducer;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/follow",method = RequestMethod.POST)
    @ResponseBody
    public String follow(int userId)
    {
        followService.follow(userId);
        //触发关注事件
        if(followService.findIsFollowed(userId)) {
            Event event = new Event();
            event.setTopic(TOPIC_FOLLOW);
            event.setUserId(hostHolder.getUser().getId());
            event.setEntityUserId(userId);

            eventProducer.fireEvent(event);
        }

        return CommunityUtil.getJsonString(0,null,null);
    }
}

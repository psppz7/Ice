package com.nowcoder.community.controller;

import com.nowcoder.community.service.LikeService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LikeController {

    @Autowired
    LikeService likeService;

    @Autowired
    HostHolder hostHolder;

    @RequestMapping(path = "/like",method = RequestMethod.POST)
    @ResponseBody
    public String Like(int entityType,int entityId)
    {
        likeService.Like(hostHolder.getUser().getId(),entityType,entityId);
        long likeCount = likeService.findEntityLikeCount(entityType,entityId);
        int status = likeService.findEntityLikeStatus(hostHolder.getUser().getId(),entityType,entityId);

        Map<String,Object> map = new HashMap<>();
        map.put("likeCount",likeCount);
        map.put("likeStatus",status);

        return CommunityUtil.getJsonString(0,null,map);
    }
}

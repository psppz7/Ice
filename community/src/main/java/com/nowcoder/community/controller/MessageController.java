package com.nowcoder.community.controller;

import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.Page;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.service.UserService;
import com.nowcoder.community.util.CommunityUtil;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class MessageController {
    @Autowired
    MessageService messageService;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    //获取私信界面
    @RequestMapping(path = "/message",method = RequestMethod.GET)
    public String getMessagePage(Model model, Page page)
    {
         User user = hostHolder.getUser();
        page.setPath("/message");
        page.setRows((messageService.findConversationCount(user.getId())));
        page.setLimit(5);
        int unReadLetterCount = messageService.findUnreadLetterCount(user.getId());
        List<Map<String,Object>> list =  messageService.getMessagePage(user.getId(), page.getOffset(), page.getLimit());
        model.addAttribute("conversations",list);
        model.addAttribute("unReadLetterCount",unReadLetterCount);
         return "site/letter";
    }

    //获取私信具体界面
    @RequestMapping(path = "/message/detail/{id}",method = RequestMethod.GET)
    public String getMessageDetail(@PathVariable("id") String conversationId,Page page,Model model)
    {
        User user = hostHolder.getUser();
        page.setPath("/message/detail/"+conversationId);
        page.setRows(messageService.findLetterCount(conversationId));
        page.setLimit(5);
        List<Map<String,Object>> list = messageService.getMessageDetail(conversationId,page.getOffset(), page.getLimit());

        Message message = (Message) list.get(0).get("message");

        if(user.getId() == message.getFromId())
        {
            User fromUser = userService.findUserById(message.getToId());
            model.addAttribute("fromUsername",fromUser.getUsername());
        }
        else
        {
            model.addAttribute("fromUsername",user.getUsername());
        }
        model.addAttribute("letters",list);
        return "site/letter-detail";
    }

    //发私信
    @RequestMapping(path = "/send/message",method = RequestMethod.POST)
    @ResponseBody
    public String sendMessage(String toUsername,String content)
    {

        int code = messageService.sendMessage(toUsername,content,hostHolder.getUser().getId());
        if(code==1)
            return CommunityUtil.getJsonString(1,"发送成功",null);
        else
            return CommunityUtil.getJsonString(-1,"发送失败",null);
    }

    //获取通知界面
    @RequestMapping(path = "/notice",method = RequestMethod.GET)
    public String getNoticePage(Model model)
    {
        Map<String,Object> map =  messageService.getNoticePage();
        model.addAttribute("map",map);

        return "/site/notice";
    }
    //获取通知具体界面
    @RequestMapping(path = "/notice/detail/{type}",method = RequestMethod.GET)
    public String getNoticeDetail(Model model,Page page,@PathVariable("type") String type )
    {
        List<Map<String,Object>> list = messageService.getNoticeDetail(type,page.getOffset(),page.getLimit());
        model.addAttribute("list",list);

        return "/site/notice-detail";
    }
}

package com.nowcoder.community.service;

import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserMapper userMapper;

    public List<Map<String,Object>> getMessagePage(int userId, int offset, int limit)
    {
        List<Map<String,Object>> list = new ArrayList<>();

        List<Message> conversations = messageMapper.selectConversations(userId,offset,limit);
        for(Message m:conversations)
        {
            Map<String,Object> map = new HashMap<>();
            User user;
            int letterCount = messageMapper.selectLetterCount(m.getConversationId());
            if(m.getFromId()==userId)
            {
                user = userMapper.selectById(m.getToId());
            }
            else
            {
                user = userMapper.selectById(m.getFromId());
            }
            int unReadLetterCount = messageMapper.selectLetterUnreadCount(userId,m.getConversationId());
            map.put("conversation",m);
            map.put("user",user);
            map.put("letterCount",letterCount);
            map.put("unReadLetterCount",unReadLetterCount);
            list.add(map);
        }
       return list;
    }

    public int findConversationCount(int userId)
    {
        return messageMapper.selectConversationCount(userId);
    }

    public int findUnreadLetterCount(int userId)
    {
        return messageMapper.selectLetterUnreadCount(userId,null);
    }

    public List<Map<String,Object>> getMessageDetail(String conversationId,int offset,int limit)
    {
        List<Map<String,Object>> list = new ArrayList<>();

        List<Message> messages = messageMapper.selectLetters(conversationId,offset,limit);

        for(Message m : messages)
        {
            Map<String,Object> map = new HashMap<>();
            User user = userMapper.selectById(m.getFromId());

            map.put("user",user);
            map.put("message",m);

            list.add(map);
        }


        return list;
    }

    public int findLetterCount(String conversationId)
    {
        return messageMapper.selectLetterCount(conversationId);
    }

    public int sendMessage(String toUsername,String content,int fromUserId)
    {
        Message message = new Message();
        User toUser = userMapper.selectByName(toUsername);
        if(toUser==null)
            return -1;
        int toUserId = toUser.getId();
        String conversationId = String.valueOf(Math.min(toUserId,fromUserId)) +"_"+String.valueOf(Math.max(toUserId,fromUserId));

        message.setToId(toUserId);
        message.setFromId(fromUserId);
        message.setStatus(0);
        message.setCreateTime(new Date());
        message.setContent(content);
        message.setConversationId(conversationId);

       return messageMapper.insertLetter(message);
    }



}

package com.nowcoder.community.service;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.dao.MessageMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MessageService implements CommunityConstant {

    @Autowired
    MessageMapper messageMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    HostHolder hostHolder;

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

    public int addMessage(Message message)
    {
       return messageMapper.insertLetter(message);
    }

    public Map<String,Object> getNoticePage()
    {
        User user = hostHolder.getUser();

        Map<String,Object> map = new HashMap<>();
        int likeNoticeCount = messageMapper.selectNoticeCount(user.getId(),TOPIC_LIKE);
        int commentNoticeCount = messageMapper.selectNoticeCount(user.getId(),TOPIC_COMMENT);
        int followNoticeCount = messageMapper.selectNoticeCount(user.getId(),TOPIC_FOLLOW);

        int likeUnreadNoticeCount = messageMapper.selectNoticeUnreadCount(user.getId(),TOPIC_LIKE);
        int commentUnreadNoticeCount = messageMapper.selectNoticeUnreadCount(user.getId(),TOPIC_COMMENT);
        int followUnreadNoticeCount = messageMapper.selectNoticeUnreadCount(user.getId(),TOPIC_FOLLOW);

        Message lastLikeNotice = messageMapper.selectLastNotice(user.getId(),TOPIC_LIKE);
        Message lastCommentNotice = messageMapper.selectLastNotice(user.getId(),TOPIC_COMMENT);
        Message lastFollowNotice = messageMapper.selectLastNotice(user.getId(),TOPIC_FOLLOW);

        Map<String,Object> lastLikeContent = JSONObject.parseObject(lastLikeNotice.getContent(),Map.class);
        Map<String,Object> lastCommentContent = JSONObject.parseObject(lastCommentNotice.getContent(),Map.class);
        Map<String,Object> lastFollowContent = JSONObject.parseObject(lastFollowNotice.getContent(),Map.class);

        int lastLikeUserId = (int)lastLikeContent.get("userId");
        int lastCommentUserId = (int)lastCommentContent.get("userId");
        int lastFollowUserId = (int)lastFollowContent.get("userId");

        Date lastLikeDate = lastLikeNotice.getCreateTime();
        Date lastCommentDate = lastCommentNotice.getCreateTime();
        Date lastFollowDate = lastFollowNotice.getCreateTime();

        User lastLikeUser = userMapper.selectById(lastLikeUserId);
        User lastCommentUser = userMapper.selectById(lastCommentUserId);
        User lastFollowUser = userMapper.selectById(lastFollowUserId);

        int lastLikeEntityType = (int) lastLikeContent.get("entityType");
        int lastCommentEntityType = (int) lastCommentContent.get("entityType");

        map.put("likeNoticeCount",likeNoticeCount);
        map.put("commentNoticeCount",commentNoticeCount);
        map.put("followNoticeCount",followNoticeCount);

        map.put("likeUnreadNoticeCount",likeUnreadNoticeCount);
        map.put("commentUnreadNoticeCount",commentUnreadNoticeCount);
        map.put("followUnreadNoticeCount",followUnreadNoticeCount);

        map.put("lastLikeUser",lastLikeUser);
        map.put("lastCommentUser",lastCommentUser);
        map.put("lastFollowUser",lastFollowUser);

        map.put("lastLikeEntityType",lastLikeEntityType);
        map.put("lastCommentEntityType",lastCommentEntityType);

        map.put("lastLikeDate",lastLikeDate);
        map.put("lastCommentDate",lastCommentDate);
        map.put("lastFollowDate",lastFollowDate);


        return map;
    }

    public List<Map<String,Object>> getNoticeDetail(String type,int offset,int limit)
    {
        List<Map<String,Object>> detail = new ArrayList<>();
        List<Message> list;
        User user = hostHolder.getUser();
        if(type.equals("like"))
        {

           list = messageMapper.selectLikeNotices(user.getId(),offset,limit);
            for(Message msg:list)
            {
                int id = msg.getId();
                messageMapper.updateNoticeStatus(id,1);
            }
            list = messageMapper.selectLikeNotices(user.getId(),offset,limit);
           for(Message msg:list)
           {
               Map<String,Object> map = new HashMap<>();
               Map<String,Object> content = JSONObject.parseObject(msg.getContent(),Map.class);
               User user1 = userMapper.selectById((int)content.get("userId"));
               int discussPostId = (int)content.get("discussPostId");
               int entityType = (int)content.get("entityType");
               map.put("entityType",entityType);
               map.put("user",user1);
               map.put("msg",msg);
               map.put("discussPostId",discussPostId);

               detail.add(map);
           }
        }
        else if(type.equals("comment"))
        {
            list = messageMapper.selectCommentNotices(user.getId(),offset,limit);
            for(Message msg:list)
            {
                int id = msg.getId();
                messageMapper.updateNoticeStatus(id,1);
            }
            list = messageMapper.selectCommentNotices(user.getId(),offset,limit);
            for(Message msg:list)
            {
                Map<String,Object> map = new HashMap<>();
                Map<String,Object> content = JSONObject.parseObject(msg.getContent(),Map.class);
                User user1 = userMapper.selectById((int)content.get("userId"));
                int discussPostId = (int)content.get("discussPostId");
                int entityType = (int)content.get("entityType");
                map.put("entityType",entityType);
                map.put("user",user1);
                map.put("msg",msg);
                map.put("discussPostId",discussPostId);
                detail.add(map);
            }
        }
        else if(type.equals("follow"))
        {
            list = messageMapper.selectFollowNotices(user.getId(),offset,limit);
            for(Message msg:list)
            {
                int id = msg.getId();
                messageMapper.updateNoticeStatus(id,1);
            }
            list = messageMapper.selectFollowNotices(user.getId(),offset,limit);
            for(Message msg:list)
            {
                Map<String,Object> map = new HashMap<>();
                Map<String,Object> content = JSONObject.parseObject(msg.getContent(),Map.class);
                User user1 = userMapper.selectById((int)content.get("userId"));
                System.out.println(msg.getConversationId());
                map.put("entityType",null);
                map.put("user",user1);
                map.put("msg",msg);
                map.put("discussPostId",null);
                detail.add(map);
            }
        }

        return detail;
    }

}

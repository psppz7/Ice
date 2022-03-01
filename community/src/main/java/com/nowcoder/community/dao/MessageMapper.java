package com.nowcoder.community.dao;

import com.nowcoder.community.entity.Message;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
import java.util.List;

@Mapper
public interface MessageMapper {

    //针对每个回话返回最新的一条私信
    List<Message> selectConversations(int userId,int offset,int limit);

    //查询当前用户的会话数量
    int selectConversationCount(int userId);

    //查询特定会话的私信
    List<Message> selectLetters(String conversationId,int offset,int limit);

    //查询特定会话包含的私信数量
    int selectLetterCount(String conversationId);

    //查询未读的消息数量
    int selectLetterUnreadCount(int userId,String conversationId);

    //增加消息
    int insertLetter(Message message);
}

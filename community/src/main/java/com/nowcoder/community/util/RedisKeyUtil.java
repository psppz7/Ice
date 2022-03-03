package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPILIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity"; //点赞的key的前缀
    private static final String PREFIX_FOLLOWEE = "followee";
    private static final String PREFIX_FOLLOWER = "follower";
    private static final String PREFIX_KAPTHCA = "kaptcha";
    private static final String PREFIX_TICKET = "ticket";
    private static final String PREFIX_USER = "user:";
    //key的格式为 like:entity:entityType:entityId
    public static String getEntityLikeKey(int entityType,int entityId)
    {
        return PREFIX_ENTITY_LIKE + SPILIT + entityType + SPILIT + entityId;
    }

    public static String getUserLikeKey(int userId,int entityType)
    {
        return userId+SPILIT+entityType;
    }

    public static String getFolloweeKey(int userId)  //zset  当前用户关注的人
    {
        return PREFIX_FOLLOWEE + SPILIT + userId;
    }

    public static String getFollowerKey(int userId)  //zset  关注当前用户的人
    {
        return PREFIX_FOLLOWER + SPILIT + userId;
    }

    public static String getKapthcaKey(String owner) //给客户端发一串字符用来识别
    {
        return PREFIX_KAPTHCA + SPILIT + owner;
    }

    public static String getTicketKey(String ticket) //给客户端发一串字符用来识别
    {
        return PREFIX_TICKET+ SPILIT + ticket;
    }
    public static String getUsertKey(int userId) //给客户端发一串字符用来识别
    {
        return PREFIX_USER+ SPILIT + userId;
    }
}

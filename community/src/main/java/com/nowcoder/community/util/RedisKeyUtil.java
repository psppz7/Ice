package com.nowcoder.community.util;

public class RedisKeyUtil {
    private static final String SPILIT = ":";
    private static final String PREFIX_ENTITY_LIKE = "like:entity"; //点赞的key的前缀

    //key的格式为 like:entity:entityType:entityId
    public static String getEntityLikeKey(int entityType,int entityId)
    {
        return PREFIX_ENTITY_LIKE + SPILIT + entityType + SPILIT + entityId;
    }

    public static String getUserLikeKey(int userId,int entityType)
    {
        return String.valueOf(userId+SPILIT+entityType);
    }
}

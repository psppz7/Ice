package com.nowcoder.community.util;

public interface CommunityConstant {
    int ACTIVATION_SUCCESS = 0;  //激活成功
    int ACTIVATION_REAPEAT = 1;  //重复激活
    int ACTIVATION_FAILURE = 2;  //激活失败
    //登录凭证有效时间
    int DEFAULT_EXPIRED = 3600*12; //默认
    int REMEMBER_EXPIRED = 3600*24*7; //记住

    //实体类型：帖子
    int ENTITY_TYPE_POST = 1;
    //实体类型：评论
    int ENTITY_TYPE_COMMENT = 2;

    String CONTEXT_PATH = "/community";

    //事件主题

    //评论
    String TOPIC_COMMENT = "comment";

    //点赞
    String TOPIC_LIKE = "like";

    //关注
    String TOPIC_FOLLOW = "follow";

    String TOPIC_PUBLISH = "publish";

    String TOPIC_DELETE = "delete";

    //系统用户id
    int SYSTEM_USERID = 1;

    // 权限普通用户
    String AUTHORITY_USER = "user";

    // 权限管理员
    String AUTHORITY_ADMIN = "admin";

    // 权限版主
    String AUTHORITY_MODERATOR = "moderator";

}

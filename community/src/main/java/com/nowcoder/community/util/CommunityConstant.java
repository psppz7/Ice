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
}

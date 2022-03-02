package com.nowcoder.community.service;

import com.nowcoder.community.entity.Comment;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.CommunityConstant;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service  //点赞业务
public class LikeService implements CommunityConstant {

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    CommentService commentService;

    @Autowired
    UserService userService;

    //点赞
    public void Like(int userId,int entityType,int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
       boolean isMember = redisTemplate.opsForSet().isMember(entityLikeKey,userId);//判断是否已经在集合中
        if(isMember)
        {
            redisTemplate.opsForSet().remove(entityLikeKey,userId);
            if(entityType==ENTITY_TYPE_POST)
            {
                DiscussPost post = discussPostService.findDiscussPostsById(entityId);
                User user = userService.findUserById(post.getUserId());
                String userKey = RedisKeyUtil.getUserLikeKey(user.getId(),entityType);
                redisTemplate.opsForSet().remove(userKey,userId);
            }
            else
            {
                Comment comment = commentService.findCommentById(entityId);
                User user = userService.findUserById(comment.getUserId());
                String userKey = RedisKeyUtil.getUserLikeKey(user.getId(),entityType);
                redisTemplate.opsForSet().remove(userKey,userId);
            }
        }
        else
        {
            redisTemplate.opsForSet().add(entityLikeKey,userId);
            if(entityType==ENTITY_TYPE_POST)
            {
                DiscussPost post = discussPostService.findDiscussPostsById(entityId);
                User user = userService.findUserById(post.getUserId());
                String userKey = RedisKeyUtil.getUserLikeKey(user.getId(),entityType);
                redisTemplate.opsForSet().add(userKey,userId);
            }
            else
            {
                Comment comment = commentService.findCommentById(entityId);
                User user = userService.findUserById(comment.getUserId());
                String userKey = RedisKeyUtil.getUserLikeKey(user.getId(),entityType);
                redisTemplate.opsForSet().add(userKey,userId);
            }
        }
    }

    //查询某实体点赞的数量
    public long findEntityLikeCount(int entityType,int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
        System.out.println(entityLikeKey);
        return redisTemplate.opsForSet().size(entityLikeKey);
    }

    //查询某人对某实体的点赞状态
    public int findEntityLikeStatus(int userId,int entityType,int entityId)
    {
        String entityLikeKey = RedisKeyUtil.getEntityLikeKey(entityType,entityId);
      if(redisTemplate.opsForSet().isMember(entityLikeKey,userId))
          return 1;
      else
          return 0;
    }

    //查询某人被点赞过的数量
    public long findUserLikeCount(int userId)
    {
        String postLikeKey = RedisKeyUtil.getUserLikeKey(userId,ENTITY_TYPE_POST);
        String commentLikeKey = RedisKeyUtil.getUserLikeKey(userId,ENTITY_TYPE_COMMENT);
        long postLikeCount = redisTemplate.opsForSet().size(postLikeKey);
        long commentLikeCount = redisTemplate.opsForSet().size(commentLikeKey);

        return postLikeCount+commentLikeCount;
    }
}

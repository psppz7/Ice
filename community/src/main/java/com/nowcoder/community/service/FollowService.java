package com.nowcoder.community.service;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.HostHolder;
import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;


@Service
public class FollowService {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    HostHolder hostHolder;

    @Autowired
    UserService userService;

    public void follow(int userId) //被关注的人的userId
    {
        User user = hostHolder.getUser();

        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations operation) throws DataAccessException {
                String followeeKey = RedisKeyUtil.getFolloweeKey(user.getId());
                String followerKey = RedisKeyUtil.getFollowerKey(userId);
                Boolean isFollowed = findIsFollowed(userId);
                operation.multi();
                if(isFollowed)
                {
                    operation.opsForZSet().remove(followeeKey,userId);           //已被关注
                    operation.opsForZSet().remove(followerKey,user.getId());
                }
                else
                {
                    operation.opsForZSet().add(followeeKey,userId,System.currentTimeMillis());  //未被关注
                    operation.opsForZSet().add(followerKey,user.getId(),System.currentTimeMillis());
                }
                return operation.exec();
            }
        });
    }
    //判断该用户是否已被自己关注
    public boolean findIsFollowed(int userId)//被关注人的
    {
        User user = hostHolder.getUser();
        String followeeKey = RedisKeyUtil.getFolloweeKey(user.getId());
        return redisTemplate.opsForZSet().score(followeeKey,userId)==null?false:true;
    }
    //查询用户关注的人的数量
    public long findFolloweeCount(int userId)
    {
        String followeeKey = RedisKeyUtil.getFolloweeKey(userId);
        return redisTemplate.opsForZSet().size(followeeKey);
    }
    //查询关注用户的人的数量
    public long findFollowerCount(int userId)
    {
        String followerKey = RedisKeyUtil.getFollowerKey(userId);
        return redisTemplate.opsForZSet().size(followerKey);
    }

    public List<User> findFolloweeList(int userId,int offset,int limmit)
    {
        List<User> users = new ArrayList<>();
          String followeeKey =  RedisKeyUtil.getFolloweeKey(userId);
          Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followeeKey,offset,offset+limmit+1);
          for(Integer id:ids)
          {
              users.add(userService.findUserById(id));
          }
          return users;
    }

    public  List<User> findFollowerList(int userId,int offset,int limmit)
    {
        List<User> users = new ArrayList<>();
        String followerKey = RedisKeyUtil.getFollowerKey(userId);
        Set<Integer> ids = redisTemplate.opsForZSet().reverseRange(followerKey,offset,offset+limmit+1);
        for(Integer id:ids)
        {
            users.add(userService.findUserById(id));
        }
        return users;
    }

}

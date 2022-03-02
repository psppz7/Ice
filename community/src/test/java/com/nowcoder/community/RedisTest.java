package com.nowcoder.community;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void test()
    {
        String key = "count";
        redisTemplate.opsForValue().set(key,1);

        System.out.println(redisTemplate.opsForValue().get(key));
    }
}

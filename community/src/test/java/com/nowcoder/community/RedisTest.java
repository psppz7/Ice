package com.nowcoder.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
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

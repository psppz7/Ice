package com.nowcoder.community;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
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

    @Test
    public void BitmapTest()
    {
        String redisKey = "yyy:";
        redisTemplate.opsForValue().set(redisKey,0);
        redisTemplate.opsForValue().setBit(redisKey,0,true);
        redisTemplate.opsForValue().setBit(redisKey,1,true);
        Object obj = redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                return connection.bitCount(redisKey.getBytes());
            }
        });
        System.out.println(obj);
    }
}

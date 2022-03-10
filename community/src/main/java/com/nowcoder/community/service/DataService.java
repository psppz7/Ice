package com.nowcoder.community.service;

import com.nowcoder.community.util.RedisKeyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class DataService {

    @Autowired
    private RedisTemplate redisTemplate;

    private SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

    //将指定IP计入uv
    public void recordUv(String ip)
    {
        String redisKey = RedisKeyUtil.getUvKey(df.format(new Date()));
        redisTemplate.opsForHyperLogLog().add(redisKey,ip);
    }
    //统计指定日期范围内的UV
    public long calulateUv(Date start,Date end)
    {
        if(start==null||end==null)
            throw new IllegalArgumentException("参数不能为空");

        List<String> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();           //对日期的遍历
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) //不晚于end
        {
            String redisKey = RedisKeyUtil.getUvKey(df.format(calendar.getTime()));
            keyList.add(redisKey);
            calendar.add(Calendar.DATE,1);  //加一天
        }

        //合并这些数据
        String redisKey = RedisKeyUtil.getUvKey(df.format(start),df.format(end)); //合并之后的Key
        redisTemplate.opsForHyperLogLog().union(redisKey,keyList.toArray());

        return redisTemplate.opsForHyperLogLog().size(redisKey);
    }
    //将指定用户计入DAU
    public void recordDau(int userId)
    {
        String redisKey = RedisKeyUtil.getDauKey(df.format(new Date()));
        redisTemplate.opsForValue().setBit(redisKey,userId,true);

    }
    //统计指定日期范围内的DAU
    public long calulateDau(Date start,Date end)
    {
        if(start==null||end==null)
            throw new IllegalArgumentException("参数不能为空");

        List<byte[]> keyList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();           //对日期的遍历
        calendar.setTime(start);
        while (!calendar.getTime().after(end)) //不晚于end
        {
            byte[] redisKey = RedisKeyUtil.getDauKey(df.format(calendar.getTime())).getBytes();

            keyList.add(redisKey);
            calendar.add(Calendar.DATE,1);  //加一天
        }
        //进行or运算
        return (long)redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                String redisKey = RedisKeyUtil.getDauKey(df.format(start),df.format(end));
                connection.bitOp(RedisStringCommands.BitOperation.OR,
                        redisKey.getBytes(),keyList.toArray(new byte[0][0]));
                long ans = connection.bitCount(redisKey.getBytes());
                return ans;
            }
        });
    }
}

package com.nowcoder.community.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class CommunityUtil {
    //生成随机字符串
    public static String generateUUID()
    {
        return UUID.randomUUID().toString().replaceAll("-",""); //随机生成uuid去掉所有符号
    }

    //MD5加密

    public static String md5(String key)
    {
        if(StringUtils.isAllBlank(key))
            return null;
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    //获取Json字符串
    public static String getJsonString(int code, String msg, Map<String,Object> map)
    {
        JSONObject json = new JSONObject();
        json.put("code",code);
        json.put("msg",msg);
        if(map!=null)
        {
            for(String s : map.keySet())
            {
                json.put(s,map.get(s));
            }
        }
        return json.toJSONString();
    }
}

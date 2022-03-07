package com.nowcoder.community.event;

import com.alibaba.fastjson.JSONObject;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.Event;
import com.nowcoder.community.entity.Message;
import com.nowcoder.community.service.DiscussPostService;
import com.nowcoder.community.service.ElasticSearchService;
import com.nowcoder.community.service.MessageService;
import com.nowcoder.community.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    MessageService messageService;

    @Autowired
    DiscussPostService discussPostService;

    @Autowired
    ElasticSearchService elasticSearchService;

    @KafkaListener(topics ={TOPIC_COMMENT,TOPIC_LIKE,TOPIC_FOLLOW} )
    public void handleMessage(ConsumerRecord record)
    {
        if(record==null||record.value()==null)
            return;
        //将获得的json字符串还原为Event对象
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null)
            return;

        //发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USERID);
        message.setToId(event.getEntityUserId());
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        Map<String,Object> content = new HashMap<>();
        content.put("userId",event.getUserId());
        content.put("entityType",event.getEntityType());
        content.put("entityId",event.getEntityId());


        //可能存在的额外数据
        if(!event.getData().isEmpty())
        {
            for(Map.Entry<String,Object> entry : event.getData().entrySet())
            {
                content.put(entry.getKey(),entry.getValue());
            }
        }

        message.setContent(JSONObject.toJSONString(content));

        messageService.addMessage(message);
    }

    @KafkaListener(topics = {TOPIC_PUBLISH})
    public void handlePublish(ConsumerRecord record)  //将发布的帖子发送到ES服务器保存
    {
        if(record==null||record.value()==null)
            return;
        Event event = JSONObject.parseObject(record.value().toString(),Event.class);
        if(event == null)
            return;

       DiscussPost post = discussPostService.findDiscussPostsById(event.getEntityId());
       elasticSearchService.saveDiscussPost(post);

    }
}

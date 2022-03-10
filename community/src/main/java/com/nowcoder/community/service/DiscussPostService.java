package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.User;
import com.nowcoder.community.util.SensitiveFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Autowired
    private UserMapper userMapper;

    public List<DiscussPost> findDiscussPosts(int userid, int offset, int limit)
    {
        return discussPostMapper.selectDiscussPosts(userid,offset,limit);
    }

    public int fineDiscussPostRows(int userid)
    {
        return discussPostMapper.selectDiscussPostRows(userid);
    }

    public int addDiscussPost(DiscussPost discussPost)
    {
        if(discussPost==null)
            throw new IllegalArgumentException("参数值不能为空");
        //过滤标签
        discussPost.setTitle(HtmlUtils.htmlEscape(discussPost.getTitle()));
        discussPost.setContent(HtmlUtils.htmlEscape(discussPost.getContent()));

        //过滤敏感词
        discussPost.setTitle(sensitiveFilter.filter(discussPost.getTitle()));
        discussPost.setContent(sensitiveFilter.filter(discussPost.getContent()));

        return discussPostMapper.insertDiscussPost(discussPost);
    }

    public Map<String,Object> showDiscussDetail(int id)
    {
        Map<String,Object> map = new HashMap<>();
        DiscussPost discussPost = discussPostMapper.selectDiscussPostById(id);

        if(discussPost!=null)
        {
            User user = userMapper.selectById(discussPost.getUserId());
            map.put("id",discussPost.getId());
            map.put("userId",discussPost.getUserId());
            map.put("title",discussPost.getTitle());
            map.put("content",discussPost.getContent());
            map.put("type",discussPost.getType());
            map.put("status",discussPost.getStatus());
            map.put("createTime",discussPost.getCreateTime());
            map.put("commentCount",discussPost.getCommentCount());
            map.put("score",discussPost.getScore());
            map.put("headerUrl",user.getHeaderUrl());
            map.put("username",user.getUsername());
        }
        return map;
    }

    public DiscussPost findDiscussPostsById(int id)
    {
        return    discussPostMapper.selectDiscussPostById(id);
    }

    public void top(int discussPostId)
    {
        DiscussPost post = discussPostMapper.selectDiscussPostById(discussPostId);
        if(post.getType()!=1)
            discussPostMapper.updateType(discussPostId,1);
        else
            discussPostMapper.updateType(discussPostId,0);

    }

    public void elite(int discussPostId)
    {
        DiscussPost post = discussPostMapper.selectDiscussPostById(discussPostId);
        if(post.getStatus()!=1)
            discussPostMapper.updateStatus(discussPostId,1);
        else
            discussPostMapper.updateStatus(discussPostId,0);
    }

    public void delete(int discussPostId)
    {
        discussPostMapper.updateStatus(discussPostId,2);
    }

}

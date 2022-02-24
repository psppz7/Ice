package com.nowcoder.community.service;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.entity.DiscussPost;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    public List<DiscussPost> findDiscussPosts(int userid, int offset, int limit)
    {
        return discussPostMapper.selectDiscussPosts(userid,offset,limit);
    }

    public int fineDiscussPostRows(int userid)
    {
        return discussPostMapper.selectDiscussPostRows(userid);
    }
}

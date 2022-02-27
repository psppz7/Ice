package com.nowcoder.community.dao;

import com.nowcoder.community.entity.DiscussPost;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DiscussPostMapper {

    List<DiscussPost> selectDiscussPosts(int userId,int offset,int limit);//分页查询帖子

    //如果参数只有一个，并且参数在if标签里拼，则必须用@param取别名
    int selectDiscussPostRows(@Param("userId") int userId);  //查询帖子的行数

    int insertDiscussPost(DiscussPost discussPost);

    DiscussPost selectDiscussPostById(int id);
}

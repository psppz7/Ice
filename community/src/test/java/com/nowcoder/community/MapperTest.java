package com.nowcoder.community;

import com.nowcoder.community.dao.DiscussPostMapper;
import com.nowcoder.community.dao.LoginTicketMapper;
import com.nowcoder.community.dao.UserMapper;
import com.nowcoder.community.entity.DiscussPost;
import com.nowcoder.community.entity.LoginTicket;
import com.nowcoder.community.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class MapperTest {
    @Autowired
    LoginTicketMapper loginTicketMapper;
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DiscussPostMapper discussPostMapper;


    @Test
    public void selectuser()
    {
        User user = userMapper.selectById(101);
        System.out.println(user);


    }
    @Test
    public void selectDiscussPost()
    {
        List<DiscussPost> list = discussPostMapper.selectDiscussPosts(0,0,10);
        for(DiscussPost d : list)
        {
            System.out.println(d);
        }
        int rows = discussPostMapper.selectDiscussPostRows(0);
        System.out.println(rows);
    }
    @Test
    public void test()
    {
        int[] nums = new int[6];
        nums[0] = -1;
        nums[1] = 0;
        nums[2] = 1;
        nums[3] = 2;
        nums[4] = -1;
        nums[5] = -4;
        threeSum(nums);
    }
    public List<List<Integer>> threeSum(int[] nums) {
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        List<Integer> list;
        for(int i=0;i<nums.length;i++)
        {
            int L = i+1;
            int R = nums.length-1;
            if(nums[i]>0)
                break;

            while(L<R)
            {
                int res = nums[i]+nums[L]+nums[R];
                if(res<0)
                {
                    while(true)
                    {
                        L++;
                        if(nums[L]!=nums[L-1])
                            break;
                    }
                }
                else if(res>0)
                {
                    while(true)
                    {
                        R--;
                        if(nums[R]!=nums[R+1])
                            break;
                    }
                }
                else if(res==0)
                {
                    list = new ArrayList<Integer>();
                    list.add(nums[i]);
                    list.add(nums[L]);
                    list.add(nums[R]);
                    ans.add(list);
                    L++;
                }
            }
        }
        return ans;
    }

    @Test
    public void testInsertloginticket()
    {
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(101);
        loginTicket.setTicket("abc");
        loginTicket.setStatus(1);
        loginTicket.setExpired(new Date(System.currentTimeMillis()));

        loginTicketMapper.insertLoginTicket(loginTicket);
    }
    @Test
    public void testSelectLoginTicket()
    {
       LoginTicket loginTicket =  loginTicketMapper.selectLoginTicket("abc");
        System.out.println(loginTicket);
    }
    @Test
    public void testUpdateLoginTicket()
    {
        loginTicketMapper.updateStatus("abc",2);
    }
    @Test
    public void testInsertDiscussPost()
    {
        DiscussPost discussPost = new DiscussPost();
        discussPost.setUserId(20);
        discussPost.setStatus(1);
        discussPost.setContent("123");
        discussPost.setCreateTime(new Date(System.currentTimeMillis()));
        discussPost.setType(1);
        discussPost.setCommentCount(2);
        discussPost.setScore(20.0);
        discussPost.setTitle("123");

        discussPostMapper.insertDiscussPost(discussPost);
    }
}

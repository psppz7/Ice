package com.nowcoder.community;

import com.nowcoder.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UtilsTest {
    @Autowired
    SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter()
    {
        System.out.println(sensitiveFilter.filter("阿斯赌%博顿撒%嫖%娼%多撒大所"));
    }
}

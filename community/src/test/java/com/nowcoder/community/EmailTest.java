package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EmailTest {

    @Autowired
    private MailClient mailClient;

    @Autowired
    private TemplateEngine templateEngine;

    @Test
    public void sendEmailTest()
    {
        mailClient.sendMail("1158383138@qq.com","test","测试");
    }
    @Test
    public void loadHtml()
    {
        Context context = new Context();
        context.setVariable("username","spz");
        String  content = templateEngine.process("/html/demo",context); //组装html代码
        mailClient.sendMail("1158383138@qq.com","html",content);
    }
}

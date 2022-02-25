package com.nowcoder.community;

import com.nowcoder.community.util.MailClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
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

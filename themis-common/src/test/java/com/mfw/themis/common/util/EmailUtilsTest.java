package com.mfw.themis.common.util;

import com.mfw.themis.common.util.email.EmailParam;
import com.mfw.themis.common.util.email.EmailUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author wenhong
 */
@Ignore
@SpringBootTest
public class EmailUtilsTest {

    @Ignore
    @Test
    public void sendTest(){

        EmailParam emailParam = new EmailParam();
        emailParam.setSenderNick("服务质量平台");
        emailParam.setEmailTo("wenhong@mafengwo.com");
        emailParam.setSubject("HelloWorld");
        emailParam.setEmailContent("测试邮件");

        EmailUtils.sendEmail(emailParam);
    }

}

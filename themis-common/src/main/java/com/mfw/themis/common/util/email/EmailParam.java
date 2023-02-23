package com.mfw.themis.common.util.email;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 邮件发送参数
 * @author wenhong
 */
@Data
@NoArgsConstructor
public class EmailParam {

    /**
     * 多个接收者用英文逗号分隔
     */
    private String emailTo;
    private String subject;
    private String cc;
    private String emailContent;
    private String senderNick;

    @Override
    public String toString(){
        return "senderNick:" + getSenderNick() + "\n"
                + "subject:" + getSubject() + "\n"
                + "emailTo:" + getEmailTo() + "\n"
                + "cc:" + getCc() + "\n"
                + "content: " + getEmailContent();
    }
}

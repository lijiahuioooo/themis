package com.mfw.themis.common.util.email;

import com.alibaba.fastjson.JSONObject;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 邮件发送
 * @author wenhong
 */
public class EmailUtils {
    private static final Logger log = LoggerFactory.getLogger(EmailUtils.class);
    private static final String ALIDM_SMTP_HOST = "smtp.mafengwo.com";
    private static final String USER = "flight-itinerary@mafengwo.com";
    private static final String PASSWORD = "z96Zr3zQNdugJRGG";
    private static final String CHARSET = "text/html;charset=UTF-8";

    public EmailUtils() {
    }

    public static Boolean sendEmail(EmailParam emailParam) {
        final Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", ALIDM_SMTP_HOST);
        props.put("mail.user", USER);
        props.put("mail.password", PASSWORD);
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                String userName = props.getProperty("mail.user");
                String password = props.getProperty("mail.password");
                return new PasswordAuthentication(userName, password);
            }
        };
        Session mailSession = Session.getInstance(props, authenticator);
        MimeMessage message = new MimeMessage(mailSession);
        String failReason = "";
        boolean sendResult = true;

        try {
            String nick = "flight-itinerary@mafengwo.com";
            if (StringUtils.isNotEmpty(emailParam.getSenderNick())) {
                try {
                    nick = MimeUtility.encodeText(emailParam.getSenderNick());
                } catch (UnsupportedEncodingException var12) {
                    var12.printStackTrace();
                }
            }

            InternetAddress from = new InternetAddress("flight-itinerary@mafengwo.com", nick);
            message.setFrom(from);
            Address[] a = new Address[]{new InternetAddress("flight-itinerary@mafengwo.com")};
            message.setReplyTo(a);
            String ccListStr;
            if (StringUtils.isNotEmpty(emailParam.getEmailTo())) {
                ccListStr = getMailList(emailParam.getEmailTo().split(","));
                message.setRecipients(RecipientType.TO, InternetAddress.parse(ccListStr));
            }

            if (StringUtils.isNotEmpty(emailParam.getCc())) {
                ccListStr = getMailList(emailParam.getCc().split(","));
                message.setRecipients(RecipientType.CC, InternetAddress.parse(ccListStr));
            }

            message.setSubject(emailParam.getSubject());
            Multipart multipart = new MimeMultipart();
            BodyPart contentPart = new MimeBodyPart();
            contentPart.setContent(emailParam.getEmailContent(), CHARSET);
            multipart.addBodyPart(contentPart);
            message.setContent(multipart);
            Transport.send(message);
        } catch (MessagingException var13) {
            sendResult = false;
            log.error("发送邮件异常，param={}", JSONObject.toJSON(""), var13);
        } catch (RuntimeException var14) {
            sendResult = false;
            log.error("发送邮件附件异常", var14);
        } catch (UnsupportedEncodingException var15) {
            sendResult = false;
            log.error("设置自定义发件人姓名异常", var15);
        }

        return sendResult;
    }

    public static String getMailList(String[] mailArray) {
        StringBuffer toList = new StringBuffer();
        int length = mailArray.length;
        if (mailArray != null && length < 2) {
            toList.append(mailArray[0]);
        } else {
            for(int i = 0; i < length; ++i) {
                toList.append(mailArray[i]);
                if (i != length - 1) {
                    toList.append(",");
                }
            }
        }

        return toList.toString();
    }
}

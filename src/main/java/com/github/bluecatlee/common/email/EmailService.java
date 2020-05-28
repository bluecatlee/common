package com.github.bluecatlee.common.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

@Service("emailService")
public class EmailService {

    private final static Logger dejaVu = LoggerFactory.getLogger(EmailService.class);

    private final String TAG = "Email >>>> ";

    @Value("${email.sendmail:}")
    private String sendmail;
    @Value("${email.sendname:}")
    private String sendname;

    @Value("${email.smtpaccount:}")
    private String smtpaccount;
    @Value("${email.smtppass:}")
    private String smtppass;
    @Value("${email.smtpserver:}")
    private String smtpserver;
    @Value("${email.smtpport:}")
    private String smtpport;

    public boolean send(String subject, String receiver, String content) {
        Session session = getSession();
        session.setDebug(true);
        MimeMessage message = new MimeMessage(session);
        try {
            message.setSubject(subject);
            message.setSentDate(new Date());
            // 设置SMTP账号
            message.setFrom(new InternetAddress(getSenderName(sendname) + "<" + smtpaccount + ">"));
            message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress(receiver));
            message.setContent(content, "text/html;charset=utf-8");
            Transport.send(message);
            return true;
        } catch (Exception e) {
            dejaVu.error(TAG + "邮件发送失败", e);
            return false;
        }
    }

    private Session getSession() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", smtpserver);
        props.setProperty("mail.smtp.port", smtpport);
        props.setProperty("mail.smtp.auth", "true");
        props.put("mail.smtp.socketFactory.fallback", "true");
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpaccount, smtppass);
            }
        });
    }

    /**
     * 获得发件人 姓名
     * @param name
     * @return
     */
    private static String getSenderName(String name) {
        // 设置自定义发件人昵称
        String nick = "";
        try {
            nick = javax.mail.internet.MimeUtility.encodeText(name);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return nick;
    }
}

package com.zwsave.mail.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;

/**
 * @Author: Ja7
 * @Date: 2021-12-26 0:45
 */
@Service
public class MailService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${spring.mail.username}")
    private String from;

    @Resource
    // 使用 JavaMailSender 发送邮件
    private JavaMailSender mailSender;

    /*
    * 发送文本邮件
    * */
    public void sendSimpleMail(String to, String subject, String content) { // 发送给， 发送主题， 发送内容

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(content);
        message.setFrom(from);
        mailSender.send(message);
    }

    /*
    * 发送HTML邮件
    * */
    public void sendHtmlMail(String to, String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    /*
    * 发送附件附件邮件
    * */
    public void sendAttachmentsMail(String to, String subject, String content, String filePath) {
        final MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource file = new FileSystemResource(new File(filePath));
            String fileName = file.getFilename(); // 获取文件名
            helper.addAttachment(fileName, file); //
            helper.addAttachment(fileName + "_second", file); // 可以同时发送两个附件

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }


    /*
    * 发送图片邮件
    * */
    public void sendInlineResourceMail(String to, String subject, String content, String rscPath, String rscId) {
        logger.info("发送静态邮件开始：{}, {}, {}, {}, {}", to, subject, content, rscPath, rscId);

        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);

            FileSystemResource res = new FileSystemResource(new File(rscPath));
            helper.addInline(rscId, res);
            helper.addInline(rscId, res); // 发送两张内嵌图片
            mailSender.send(message);
            logger.info("发送静态邮件成功");
        } catch (MessagingException e) {
            logger.error("发送静态邮件失败:", e);
        }
    }

    /*
    * 模板邮件
    * */

}

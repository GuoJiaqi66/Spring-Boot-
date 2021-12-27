package com.zwsave.mail.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;

/**
 * @Author: Ja7
 * @Date: 2021-12-26 1:08
 */
@SpringBootTest
public class ServiceTest {

    @Resource
    MailService mailService;

    // 发送模板邮件
    @Resource
    TemplateEngine templateEngine; // thymeleaf专用

    @Test
    public void sendSimpleMailTest() {
        mailService.sendSimpleMail("zwsave@aliyun.com","这是我的第一封邮件", "这是我通过SpringBoot向你发送过来的邮件");
    }

    @Test
    public void sendHtmlMailTest() {
        String content =
                "<html>\n" +
                "<body>\n" +
                "<h3>hello world, 这是一封Html邮件！</h3>" +
                "</body>\n" +
                "</html>";
        mailService.sendHtmlMail("858064356@qq.com", "这是一封html邮件", content);
    }

    @Test
    public void sendAttachmentsMailTest() {
        String filePath = "D:/桌面/c盘移到d盘/mongodb_advance.7z";
        mailService.sendAttachmentsMail("858064356@qq.com", "这是一封带附件的邮件", "这封邮件带附件", filePath);

    }


    @Test
    public void sendInlineResourceMailTest() {
        String imgPath = "D:/桌面/c盘移到d盘/CY.jpg";
        String rscId = "cy"; // 给这个图片定义一个id
        String content = "<html>" +
                "<body>" +
                "这是有图片的邮件：<img src = \'cid:" + rscId + "\'> </img>" +
                "这是有图片的邮件：<img src = \'cid:" + rscId + "\'> </img>" + // 发送两张内嵌图片
                "</body>" +
                "</html>" ;
        mailService.sendInlineResourceMail("858064356@qq.com","这是一张带图片的邮件", content, imgPath, rscId);
    }


    /*
    * 模板邮件测试
    * */
    @Test
    public void templateMailTest() {
        Context context = new Context();
        context.setVariable("id", "0328"); // http://*****/doc/{id}(id=${id} id对应的id值

        String emailContext = templateEngine.process("emailTemplate", context);// 模板名， context

        mailService.sendHtmlMail("858064356@qq.com", "这是一个模板邮件", emailContext);

    }
}

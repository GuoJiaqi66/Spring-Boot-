# 1、邮件发送历史和原理

### 背景

##### ·邮件使用场景

·注册验证					·提醒、监控告警
·网站营销					·触发机制·安全的最后一道防线

##### ·Spring Boot介绍

·约定大于配置
·简单快速开发
·强大的生态链
·Spring Boot和发送邮件

##### ·邮件发送原理

###### ·邮件传输协议：SMTP协议和POP3协议

smtp：如何将一封邮件传输到另一台服务器，

pop3(邮件访问的标准协议)：如何将邮件从服务器拿下来让我们看

###### ·内容不断发展：IMAP协议和Mime协议

imap：pop3类似，不同点：客户端收取的邮件依然保存在服务器端，在客户端的操作都会返回到服务器端

mime：smtp处理二进制文件不好，所以mime诞生。现在几乎所有的smtp协议都支持mime扩展

![](D:\Z截图\Snipaste\Snipaste_2021-12-27_22-08-54.png)

##### ·前置知识

·会使用Spring 进行开发

·对Spring Boot有一定的了解

·Maven\HTML\Thymeleaf

理解邮件发送的基础知识

##### ·邮件发送历史

·1969年10月，世界上的第一封电子邮件

·1987年9月14日中国的第一封电子邮件

·30年发展历程

·Java发送邮件

·Spring发送邮件

# 2、Spring Boot和邮件系统

![](D:\Z截图\Snipaste\Snipaste_2021-12-27_22-18-14.png)



# 3、各种类型邮件的发送

#### 代码实现

·发送文本邮件

·带图片的邮件

·发送HTML邮件

·邮件模板

·发送附件邮件·邮件系统

##### Maven pom依赖

```xml
<dependencies>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
			<version>2.6.2</version>
		</dependency>
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<version>2.6.2</version>
		</dependency>
		
		
		<!-- 添加mail依赖 -->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-mail</artifactId>
			<version>2.6.1</version>
		</dependency>
		
		 <!-- 模板引擎依赖(发送模板邮件)-->
		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-thymeleaf</artifactId>
			<version>2.1.5.RELEASE</version>
		</dependency>
	</dependencies>
```





##### MailService类

```java
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

```



##### 模板邮件的thymeleaf模板

```html
<!DOCTYPE html>
<html lang="en" xmlns:th="http://www.thymeleaf.org"> <!--xmlns:th="http://www.thymeleaf.org" 一个thymeleaf的声明-->
<head>
  <meta charset="UTF-8">
  <title>邮件模板</title>
</head>
<body>
  你好，感谢您的注册，这是一封验证邮件，请点击下面的链接完成注册，感谢您的支持 <br/>
  <a href="#" th:href="@{http://www.**i.***.top/doc/{id}(id=${id})}">激活账号</a> <!--thymeleaf所有模板都已th开头-->
</body>
</html>
```





##### 测试类(发送请求)

```java
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
        context.setVariable("id", "0328"); // http://www.w**i.***.top/doc/{id}(id=${id} id对应的id值

        String emailContext = templateEngine.process("emailTemplate", context);// 模板名， context

        mailService.sendHtmlMail("858064356@qq.com", "这是一个模板邮件", emailContext);

    }
}

```





# 4、邮件系统

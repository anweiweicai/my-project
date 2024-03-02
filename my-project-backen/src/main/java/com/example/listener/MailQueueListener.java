package com.example.listener;

import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @Value("${spring.mail.nickname}")
    String nickname;

    @RabbitHandler
    public void sendMailMessage(Map<String, Object> data) {
        String type = (String)data.get("type");
        String email = (String) data.get("email");
        Integer code = (Integer) data.get("code");
        SimpleMailMessage message = switch (type){
            case "register" -> //注册
                createMessage("欢迎注册我们的网站",
                        "您的邮件注册验证码为: " + code + ", 有效时间为3分钟, 为了保障您的安全, 请勿向他人泄露验证码信息. ", email);
            case "reset" -> //重置密码
                createMessage("密码重置邮件",
                        "您好, 您正在进行重置密码操作, 验证码为: " + code + ", 有效时间为3分钟, 如非本人操作, 请无视", email);
            case "modify" ->
                createMessage("您的邮件修改验证邮件",
                        "您好, 您正在绑定新的电子邮件地址, 验证码: "+code+", 有效时间为3分钟, 如非本人操作, 请无视", email);
            default -> null; // 默认处理
        };
//        发送HTML格式的文件
//        MimeMessage message = switch (type){
//            case "register" ->
//                createHTMLMessage("欢迎注册我们的网站",
//                        """
//                                <html>
//                                <body>
//                                <h2>这是一封有历史意义的HTML邮件,请注意查收!!!</h2>
//                                </body>
//                                </html>""", email);
//            case "reset" -> //重置密码
//                createHTMLMessage("密码重置邮件",
//                        "您好, 您正在进行重置密码操作, 验证码为: " + code + ", 有效时间为3分钟, 如非本人操作, 请无视", email);
//            default -> null; // 默认处理
//        };
        if (message == null) return;
        sender.send(message);
    }

    private SimpleMailMessage createMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(nickname+'<'+username+'>');
        return message;
    }

    private MimeMessage createHTMLMessage(String title, String content, String email) throws MessagingException {
        MimeMessage message = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setSubject(title);
        helper.setText(content, true);
        helper.setTo(email);
        helper.setFrom(username);
        return message;
    }
}


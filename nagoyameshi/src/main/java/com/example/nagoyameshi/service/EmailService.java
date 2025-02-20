package com.example.nagoyameshi.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private Environment env;

    // 发送简单邮件
    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setHtml(true);
        emailSender.send(message);
    }

    // 发送 HTML 格式邮件
    public void sendHtmlMessage(String to, String subject, String htmlText) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(htmlText);
        message.setFrom(env.getProperty("spring.mail.username"));
        message.setHtml(true); 
        emailSender.send(message);
    }
}
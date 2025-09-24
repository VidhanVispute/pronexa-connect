package com.pronexa.connect.services.impl;

// import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.pronexa.connect.services.EmailService;

// import jakarta.mail.MessagingException;
// import jakarta.mail.internet.MimeMessage;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.from}") // maintain mail format
    private String fromEmail;

    @Override
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        message.setFrom(fromEmail);
        mailSender.send(message);
        System.out.println("Plain text email sent to " + to);
    }

    // @Override
    // public void sendEmailWithHtml(String to, String subject, String htmlBody) throws MessagingException {
    //     MimeMessage message = mailSender.createMimeMessage();
    //     MimeMessageHelper helper = new MimeMessageHelper(message, true); // true for multipart

    //     helper.setTo(to);
    //     helper.setSubject(subject);
    //     helper.setText(htmlBody, true); // true = HTML
    //     helper.setFrom(fromEmail);

    //     mailSender.send(message);
    //     System.out.println("HTML email sent to " + to);
    // }

    // @Override
    // public void sendEmailWithAttachment(String to, String subject, String body, File attachment) throws MessagingException {
    //     MimeMessage message = mailSender.createMimeMessage();
    //     MimeMessageHelper helper = new MimeMessageHelper(message, true);

    //     helper.setTo(to);
    //     helper.setSubject(subject);
    //     helper.setText(body);
    //     helper.setFrom(fromEmail);

    //     FileSystemResource file = new FileSystemResource(attachment);
    //     helper.addAttachment(attachment.getName(), file);

    //     mailSender.send(message);
    //     System.out.println("Email with attachment sent to " + to);
    // }
}

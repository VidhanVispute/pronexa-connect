package com.pronexa.connect.services;

// import java.io.File;

// import jakarta.mail.MessagingException;

public interface EmailService {

    // Send simple text email
    void sendEmail(String to, String subject, String body);

    // Send HTML email
    //  void sendEmailWithHtml(String to, String subject, String htmlBody) throws MessagingException;

    // Send email with attachment
    //  void sendEmailWithAttachment(String to, String subject, String body, File attachment) throws MessagingException;
}



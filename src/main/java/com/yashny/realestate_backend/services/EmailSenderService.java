package com.yashny.realestate_backend.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("homehuboff@gmail.com");
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        mailSender.send(message);
    }

    public void sendConfirmationCode(String toEmail, String confirmationCode) {
        String subject = "Ваш код подтверждения";
        String body = "Ваш код подтверждения: " + confirmationCode;

        sendEmail(toEmail, subject, body);
    }
}

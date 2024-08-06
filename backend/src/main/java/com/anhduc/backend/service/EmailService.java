package com.anhduc.backend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private JavaMailSender mailSender;

    public void sendVerificationEmail(String toEmail, String name, String verificationLink)
            throws MessagingException, UnsupportedEncodingException {
        String subject = "Please verify your registration";
        String senderName = "MyApp Support";
        String content = "<p>Dear " + name + ",</p>"
                + "<p>Please click the link below to verify your registration:</p>"
                + "<p><a href=\"" + verificationLink + "\">VERIFY</a></p>"
                + "<br>"
                + "<p>Thank you,<br>The MyApp Team</p>";

        sendEmail(toEmail, subject, senderName, content);
    }

    public void sendPasswordResetEmail(String toEmail, String name, String resetLink)
            throws MessagingException, UnsupportedEncodingException {
        String subject = "Password Reset Request";
        String senderName = "MyApp Support";
        String content = "<p>Dear " + name + ",</p>"
                + "<p>We received a request to reset your password.</p>"
                + "<p>Click the link below to reset your password:</p>"
                + "<p><a href=\"" + resetLink + "\">RESET PASSWORD</a></p>"
                + "<br>"
                + "<p>If you didn't request a password reset, please ignore this email.</p>"
                + "<br>"
                + "<p>Thank you,<br>The MyApp Team</p>";

        sendEmail(toEmail, subject, senderName, content);
    }

    private void sendEmail(String toEmail, String subject, String senderName, String content)
            throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("ducnase161841@fpt.edu.vn", senderName);
        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setText(content, true);

        mailSender.send(message);
    }
}

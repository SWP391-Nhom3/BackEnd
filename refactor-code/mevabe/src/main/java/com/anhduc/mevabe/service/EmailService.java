package com.anhduc.mevabe.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, StandardCharsets.UTF_8.name());

            // Load the template with content
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process(templateName, context);

            // Send email
            helper.setTo(to); // Email recipient
            helper.setText(html, true); // Set the HTML content
            helper.setSubject(subject); // Set the subject
            helper.setFrom("ducnase161841@fpt.edu.vn");

            javaMailSender.send(message);

        } catch (MessagingException e) {
            e.printStackTrace();
            // logger.error("Email sent with error: " + e.getMessage());
        }
    }
}

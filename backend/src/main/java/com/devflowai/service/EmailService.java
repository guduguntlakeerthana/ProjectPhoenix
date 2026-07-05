package com.devflowai.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${spring.mail.username:}")
    private String fromEmail;

    private final JavaMailSender mailSender;

    @Autowired
    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String body) {
        if (mailHost == null || mailHost.trim().isEmpty()) {
            LOGGER.warning("SMTP not configured. SIMULATED EMAIL to: " + to + "\nSubject: " + subject + "\nBody: " + body);
            return;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setFrom(fromEmail.trim().isEmpty() ? "noreply@devflowai.com" : fromEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);
            LOGGER.info("Email sent successfully to: " + to);
        } catch (Exception e) {
            LOGGER.severe("Failed to send email to " + to + ". Error: " + e.getMessage() + ". Continuing execution gracefully.");
        }
    }
}

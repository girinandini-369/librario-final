package com.librario.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Centralized email service.
 * All emails will be forced to go only to the configured admin email
 * (from application.properties: app.admin.email).
 */
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // This is the ONLY email address all messages will go to
    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Send email to the configured admin only, ignoring the original 'to'.
     */
    public void sendEmail(String ignoredTo, String subject, String body) {
        send(adminEmail, subject, body);
    }

    /**
     * Explicit send to admin (shortcut).
     */
    public void sendEmailToAdmin(String subject, String body) {
        send(adminEmail, subject, body);
    }

    /**
     * Internal common sender.
     */
    private void send(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, false);

            mailSender.send(message);
            System.out.println("✅ Email sent to: " + to + " | Subject: " + subject);
        } catch (MessagingException e) {
            throw new RuntimeException("❌ Failed to send email", e);
        }
    }
}

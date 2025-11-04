package com.example.TaskApp.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendReminderEmail(String toEmail, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("1hk21cs140@hkbk.edu.in");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
            log.info("Email sent successfully to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", toEmail, e.getMessage());
        }

        log.info("Preparing to send email to {}", toEmail);
        log.info("JavaMailSender class: {}", mailSender.getClass().getName());
    }
}
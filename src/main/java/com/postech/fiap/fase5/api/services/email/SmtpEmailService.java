package com.postech.fiap.fase5.api.services.email;

import com.postech.fiap.fase5.infrastructure.exceptions.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;
    private final MimeMessageBuilder messageBuilder;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            log.info("Sending email to: {}", to);

            MimeMessage mimeMessage = messageBuilder.build(sender, to, subject, body);
            mailSender.send(mimeMessage);

            log.info("Email sent successfully to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send email to: {}", to, e);
            throw new EmailSendingException(to, e);
        }
    }
}

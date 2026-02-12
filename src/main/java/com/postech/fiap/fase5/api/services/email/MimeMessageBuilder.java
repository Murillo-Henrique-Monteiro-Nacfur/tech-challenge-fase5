package com.postech.fiap.fase5.api.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MimeMessageBuilder {

    private static final String ENCODING = "UTF-8";
    private static final boolean HTML_ENABLED = true;
    private static final boolean MULTIPART_MODE = true;

    private final JavaMailSender mailSender;

    public MimeMessage build(String from, String to, String subject, String body) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MULTIPART_MODE, ENCODING);

        helper.setFrom(from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(body, HTML_ENABLED);

        return mimeMessage;
    }
}


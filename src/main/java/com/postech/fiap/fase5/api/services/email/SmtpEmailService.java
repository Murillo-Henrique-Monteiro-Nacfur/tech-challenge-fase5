package com.postech.fiap.fase5.api.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Primary // Define esta implementação como a preferencial
@RequiredArgsConstructor
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            log.info("Enviando e-mail para: {}", to);
            
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true); // true = HTML enabled

            mailSender.send(mimeMessage);

            log.info("E-mail enviado com sucesso para: {}", to);
        } catch (MessagingException e) {
            log.error("Erro ao enviar e-mail para: {}", to, e);
            // Dependendo da regra de negócio, poderíamos relançar a exceção ou apenas logar
            // throw new RuntimeException("Falha no envio de e-mail", e);
        }
    }
}

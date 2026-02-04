package com.postech.fiap.fase5.api.services.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
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
            
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(remetente);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            
            log.info("E-mail enviado com sucesso para: {}", to);
        } catch (Exception e) {
            log.error("Erro ao enviar e-mail para: {}", to, e);
            // Dependendo da regra de negócio, poderíamos relançar a exceção ou apenas logar
            // throw new RuntimeException("Falha no envio de e-mail", e);
        }
    }
}

package com.postech.fiap.fase5.api.services.email;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

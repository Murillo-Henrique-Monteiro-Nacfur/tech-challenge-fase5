package com.postech.fiap.fase5.infrastructure.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailSendingException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

    public EmailSendingException(String recipient, Throwable cause) {
        super(String.format("Failed to send email to: %s", recipient), cause);
    }
}


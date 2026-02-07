package com.postech.fiap.fase5.infrastructure.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ClientNotFoundException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public ClientNotFoundException(String clientId) {
        super(String.format("Client not found with clientId: %s", clientId));
    }
}


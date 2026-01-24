package com.postech.fiap.fase5.infrastructure.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailExpensesException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

    public EmailExpensesException(String message) {
        super(message);
    }
}

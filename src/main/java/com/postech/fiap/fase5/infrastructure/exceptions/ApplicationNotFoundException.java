package com.postech.fiap.fase5.infrastructure.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EmailExpensesNotFoundException extends RuntimeException {

    private final HttpStatus httpStatus = HttpStatus.NOT_FOUND;

    public EmailExpensesNotFoundException(String message) {
        super(message);
    }

}

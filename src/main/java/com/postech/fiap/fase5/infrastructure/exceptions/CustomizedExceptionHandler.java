package com.postech.fiap.fase5.infrastructure.exceptions;

import com.postech.fiap.fase5.infrastructure.exceptions.response.ExceptionResponse;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@ControllerAdvice
public class CustomizedExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, @Nonnull HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<ExceptionResponse.Fields> fieldsList = new ArrayList<>();

        ex.getAllErrors().forEach(error -> {
            String message = error.getDefaultMessage();
            String field = ((FieldError) error).getField();

            ExceptionResponse.Fields exceptionField = ExceptionResponse.Fields.builder().field(field).validation(message).build();

            fieldsList.add(exceptionField);
        });

        ExceptionResponse<ExceptionResponse.Fields> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString()
                , "Validation failed for one or more argument"
                , request.getDescription(false));

        exceptionResponse.setFields(fieldsList);
        return super.handleExceptionInternal(ex, exceptionResponse, headers, status, request);
    }

    @ExceptionHandler(EmailExpensesException.class)
    public final ResponseEntity<ExceptionResponse<Object>> handleSecureFlightExceptions(EmailExpensesException emailExpensesException) {
        ExceptionResponse<Object> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString()
                , "SecureFlight error"
                , emailExpensesException.getMessage()
        );

        return new ResponseEntity<>(exceptionResponse, emailExpensesException.getHttpStatus());
    }

    @ExceptionHandler(ApplicationNotFoundException.class)
    public final ResponseEntity<ExceptionResponse<Object>> handleSecureFlightNotFoundExceptions(ApplicationNotFoundException applicationNotFoundException) {
        ExceptionResponse<Object> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString()
                , "SecureFlight Not Found error"
                , applicationNotFoundException.getMessage()
        );

        return new ResponseEntity<>(exceptionResponse, applicationNotFoundException.getHttpStatus());
    }

    @ExceptionHandler(ClientNotFoundException.class)
    public final ResponseEntity<ExceptionResponse<Object>> handleClientNotFoundException(ClientNotFoundException clientNotFoundException) {
        ExceptionResponse<Object> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString()
                , "Client Not Found"
                , clientNotFoundException.getMessage()
        );

        return new ResponseEntity<>(exceptionResponse, clientNotFoundException.getHttpStatus());
    }

    @ExceptionHandler(EmailSendingException.class)
    public final ResponseEntity<ExceptionResponse<Object>> handleEmailSendingException(EmailSendingException emailSendingException) {
        ExceptionResponse<Object> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString()
                , "Email Sending Error"
                , emailSendingException.getMessage()
        );

        return new ResponseEntity<>(exceptionResponse, emailSendingException.getHttpStatus());
    }

    @ExceptionHandler(value = {InvalidDataAccessResourceUsageException.class, DataIntegrityViolationException.class})
    public final ResponseEntity<ExceptionResponse<Object>> handleAllExceptions(NonTransientDataAccessException dataIntegrityViolationException) {
        log.error("DataBase Integrity error: {}", dataIntegrityViolationException.getCause().getCause().getMessage());
        ExceptionResponse<Object> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString()
                , "Internal error"
                , "DataBase error"
        );

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpServerErrorException.InternalServerError.class)
    public final ResponseEntity<ExceptionResponse<Object>> handleAllInternalServerErrorExceptions(HttpServerErrorException.InternalServerError dataIntegrityViolationException) {
        ExceptionResponse<Object> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString()
                , "Exception error"
                , dataIntegrityViolationException.getCause().getCause().getMessage()
        );

        return new ResponseEntity<>(exceptionResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<ExceptionResponse<Object>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        log.warn("Access Denied: {} for request: {}", ex.getMessage(), request.getDescription(false));
        ExceptionResponse<Object> exceptionResponse = new ExceptionResponse<>(
                LocalDateTime.now().toString(),
                "Access Denied",
                "You do not have the required permissions to access this resource."
        );
        return new ResponseEntity<>(exceptionResponse, HttpStatus.FORBIDDEN);
    }

}

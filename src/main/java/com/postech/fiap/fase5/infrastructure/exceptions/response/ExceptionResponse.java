package com.postech.fiap.fase5.infrastructure.exceptions.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class ExceptionResponse<T> {
    private String timestamp;
    private String message;
    private String details;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<T> fields;

    public ExceptionResponse(String timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class Fields {
        private String field;
        private String validation;
    }

}
package com.postech.fiap.fase5.api.services.email.template;

public interface EmailTemplateBuilder<T> {

    String build(String nomePonto, T items);
}


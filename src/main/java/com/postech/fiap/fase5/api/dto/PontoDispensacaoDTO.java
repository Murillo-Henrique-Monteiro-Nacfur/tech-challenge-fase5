package com.postech.fiap.fase5.api.dto;

public record PontoDispensacaoDTO(
        Long id,
        String cnes,
        String nome,
        String tipo,
        String emailResponsavel,
        Long clientId
) {}

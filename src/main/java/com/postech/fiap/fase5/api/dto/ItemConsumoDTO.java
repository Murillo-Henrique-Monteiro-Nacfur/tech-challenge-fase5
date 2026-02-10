package com.postech.fiap.fase5.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ItemConsumoDTO(
        Long loteId,
        Integer quantidadeConsumida,
        LocalDateTime dataHoraEvento
) {}

package com.postech.fiap.fase5.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record ItemConsumoDTO(
        Long loteId, // Assumindo Long conforme banco, mas JSON pode vir String e Jackson converte se for numérico
        Integer quantidadeConsumida,
        LocalDateTime dataHoraEvento
) {}

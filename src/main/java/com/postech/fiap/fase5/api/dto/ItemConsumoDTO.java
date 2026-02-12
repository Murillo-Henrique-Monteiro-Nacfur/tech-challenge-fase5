package com.postech.fiap.fase5.api.dto;

import java.time.LocalDateTime;

public record ItemConsumoDTO(
        String numeroLote,
        Integer quantidadeConsumida,
        LocalDateTime dataHoraEvento
) {
}

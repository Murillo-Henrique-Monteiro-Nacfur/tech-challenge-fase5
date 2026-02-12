package com.postech.fiap.fase5.api.dto;

import java.time.LocalDate;

public record ItemCargaDTO(
        String catmat,
        String idLoteExterno,
        String numeroLote,
        LocalDate dataValidade,
        LocalDate dataFabricacao,
        Integer quantidadeEnviada,
        Integer quantidadeTotalLote
) {}

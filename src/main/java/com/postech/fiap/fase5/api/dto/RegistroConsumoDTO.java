package com.postech.fiap.fase5.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record RegistroConsumoDTO(
        Long pontoDispensacaoId,
        List<ItemConsumoDTO> listaConsumo
) {}

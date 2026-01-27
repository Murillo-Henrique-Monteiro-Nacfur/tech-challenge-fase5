package com.postech.fiap.fase5.api.dto.insumos;

public record InsumoDetalheDTO(
        String id,
        String nome,
        String formaFarmaceutica,
        String marca,
        String descricao
) {}

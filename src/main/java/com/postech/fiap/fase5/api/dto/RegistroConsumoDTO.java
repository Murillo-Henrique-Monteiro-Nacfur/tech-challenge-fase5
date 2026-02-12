package com.postech.fiap.fase5.api.dto;

import java.util.List;

public record RegistroConsumoDTO(
        String cnesPontoDispensacao,
        List<ItemConsumoDTO> listaConsumo
) {
}

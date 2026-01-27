package com.postech.fiap.fase5.api.dto;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CargaEstoqueDTO(
        String cnesPontoDispensacao,
        LocalDateTime dataCarga,
        List<ItemCargaDTO> itens,
        List<InsumoDetalheDTO> insumosDetalhes
) {}

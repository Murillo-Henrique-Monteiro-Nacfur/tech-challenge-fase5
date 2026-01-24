package com.postech.fiap.fase5.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.postech.fiap.fase5.api.entities.Insumo;

public record InsumoDTO(
        Long id,

        String codigoCatmat,
        String nomeGenerico,
        String formaFarmaceutica,
        String marca,
        String descricao
) {
    public static InsumoDTO fromEntity(Insumo insumo) {
        return new InsumoDTO(
                insumo.getId(),
                insumo.getCodigoCatmat(),
                insumo.getNomeGenerico(),
                insumo.getFormaFarmaceutica(),
                insumo.getMarca(),
                insumo.getDescricao()
        );
    }

    public Insumo toEntity() {
        return new Insumo(
                this.id,
                this.codigoCatmat,
                this.nomeGenerico,
                this.formaFarmaceutica,
                this.marca,
                this.descricao
        );
    }
}

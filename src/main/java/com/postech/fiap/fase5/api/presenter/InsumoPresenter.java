package com.postech.fiap.fase5.api.presenter;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import org.springframework.stereotype.Component;

@Component
public class InsumoPresenter {

    public Insumo toEntity(InsumoDetalheDTO dto) {
        Insumo insumo = new Insumo();
        insumo.setCodigoCatmat(dto.id());
        insumo.setNomeGenerico(dto.nome());
        insumo.setFormaFarmaceutica(dto.formaFarmaceutica());
        return insumo;
    }

    public Insumo toEntity(InsumoDTO dto) {
        return new Insumo(
                dto.id(),
                dto.codigoCatmat(),
                dto.nomeGenerico(),
                dto.formaFarmaceutica(),
                dto.marca(),
                dto.descricao()
        );
    }

    public InsumoDTO toDto(Insumo entity) {
        return new InsumoDTO(
                entity.getId(),
                entity.getCodigoCatmat(),
                entity.getNomeGenerico(),
                entity.getFormaFarmaceutica(),
                entity.getMarca(),
                entity.getDescricao()
        );
    }
}

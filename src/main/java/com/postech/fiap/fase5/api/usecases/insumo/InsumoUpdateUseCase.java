package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.InsumoDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsumoUpdateUseCase {

    private final InsumoRepository insumoRepository;

    public InsumoDTO execute(Long id, InsumoDTO insumoDTO) {
        Insumo existingInsumo = insumoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado com ID: " + id));

        existingInsumo.setCodigoCatmat(insumoDTO.codigoCatmat());
        existingInsumo.setNomeGenerico(insumoDTO.nomeGenerico());
        existingInsumo.setFormaFarmaceutica(insumoDTO.formaFarmaceutica());
        existingInsumo.setMarca(insumoDTO.marca());
        existingInsumo.setDescricao(insumoDTO.descricao());

        return InsumoDTO.fromEntity(insumoRepository.save(existingInsumo));
    }
}

package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.presenter.InsumoPresenter;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import com.postech.fiap.fase5.infrastructure.exceptions.ApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsumoUpdateUseCase {

    private final InsumoRepository insumoRepository;
    private final InsumoPresenter insumoPresenter;

    public InsumoDTO execute(Long id, InsumoDTO insumoDTO) {
        Insumo existingInsumo = findInsumoById(id);
        updateInsumoData(existingInsumo, insumoDTO);
        Insumo updatedInsumo = insumoRepository.save(existingInsumo);
        return insumoPresenter.toDto(updatedInsumo);
    }

    private Insumo findInsumoById(Long id) {
        return insumoRepository.findById(id)
                .orElseThrow(() -> new ApplicationNotFoundException("Insumo não encontrado com ID: " + id));
    }

    private void updateInsumoData(Insumo insumo, InsumoDTO dto) {
        insumo.setCodigoCatmat(dto.codigoCatmat());
        insumo.setNomeGenerico(dto.nomeGenerico());
        insumo.setFormaFarmaceutica(dto.formaFarmaceutica());
        insumo.setMarca(dto.marca());
        insumo.setDescricao(dto.descricao());
    }
}

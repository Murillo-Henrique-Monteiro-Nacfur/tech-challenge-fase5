package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.presenter.InsumoPresenter;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsumoCreateUpdateUseCase {

    private final InsumoRepository insumoRepository;
    private final InsumoPresenter insumoPresenter;

    public void execute(List<InsumoDetalheDTO> insumosDetalhes) {
        if (insumosDetalhes == null) return;

        for (InsumoDetalheDTO dto : insumosDetalhes) {
            // Verifica se já existe pelo ID externo (mapeado para codigoCatmat)
            if (!insumoRepository.existsByCodigoCatmat(dto.id())) {
                Insumo insumo = insumoPresenter.toEntity(dto);
                insumoRepository.save(insumo);
            }
        }
    }
}

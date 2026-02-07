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
        if (isInvalidList(insumosDetalhes)) {
            return;
        }

        insumosDetalhes.stream()
                .filter(this::isNewInsumo)
                .forEach(this::createAndSaveInsumo);
    }

    private boolean isInvalidList(List<InsumoDetalheDTO> insumosDetalhes) {
        return insumosDetalhes == null || insumosDetalhes.isEmpty();
    }

    private boolean isNewInsumo(InsumoDetalheDTO insumoDetalhe) {
        return !insumoRepository.existsByCodigoCatmat(insumoDetalhe.id());
    }

    private void createAndSaveInsumo(InsumoDetalheDTO insumoDetalhe) {
        Insumo insumo = insumoPresenter.toEntity(insumoDetalhe);
        insumoRepository.save(insumo);
    }
}

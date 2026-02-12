package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.presenter.InsumoPresenter;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class InsumoCreateUpdateUseCase {

    private final InsumoRepository insumoRepository;
    private final InsumoPresenter insumoPresenter;

    public List<Insumo> execute(List<InsumoDetalheDTO> insumosDetalhes) {
        if (isInvalidList(insumosDetalhes)) {
            return Collections.emptyList();
        }
        List<Insumo> insumos = new ArrayList<>();
        for(var insumo : insumosDetalhes){
            insumoRepository.findByCodigoCatmat(insumo.catmat())
                    .ifPresent(insumos::add);
            if(isNewInsumo(insumo)){
                Insumo newInsumo = createAndSaveInsumo(insumo);
                insumos.add(insumoRepository.saveAndFlush(newInsumo));
            }

        }
        return insumos;
    }

    private boolean isInvalidList(List<InsumoDetalheDTO> insumosDetalhes) {
        return insumosDetalhes == null || insumosDetalhes.isEmpty();
    }

    private boolean isNewInsumo(InsumoDetalheDTO insumoDetalhe) {
        return !insumoRepository.existsByCodigoCatmat(insumoDetalhe.catmat());
    }

    private Insumo createAndSaveInsumo(InsumoDetalheDTO insumoDetalhe) {
        Insumo insumo = insumoPresenter.toEntity(insumoDetalhe);
        return insumoRepository.saveAndFlush(insumo);
    }
}

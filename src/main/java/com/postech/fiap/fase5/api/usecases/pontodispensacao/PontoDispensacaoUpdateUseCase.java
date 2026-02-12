package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.presenter.PontoDispensacaoPresenter;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PontoDispensacaoUpdateUseCase {

    private final PontoDispensacaoRepository repository;
    private final PontoDispensacaoPresenter presenter;

    public PontoDispensacaoDTO execute(Long id, PontoDispensacaoDTO dto) {
        PontoDispensacao existing = findPontoDispensacaoById(id);
        updatePontoDispensacaoData(existing, dto);
        PontoDispensacao updated = repository.save(existing);
        return presenter.toDto(updated);
    }

    private PontoDispensacao findPontoDispensacaoById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ponto de Dispensação não encontrado com ID: " + id));
    }

    private void updatePontoDispensacaoData(PontoDispensacao entity, PontoDispensacaoDTO dto) {
        entity.setCnes(dto.cnes());
        entity.setNome(dto.nome());
        entity.setTipo(dto.tipo());
        entity.setEmailResponsavel(dto.emailResponsavel());
    }
}

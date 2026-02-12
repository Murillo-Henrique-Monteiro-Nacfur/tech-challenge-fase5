package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.presenter.PontoDispensacaoPresenter;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PontoDispensacaoReadUseCase {

    private final PontoDispensacaoRepository repository;
    private final PontoDispensacaoPresenter presenter;

    public Page<PontoDispensacaoDTO> findAll(Pageable pageable) {
        Page<PontoDispensacao> entities = repository.findAll(pageable);
        return convertPageToDto(entities);
    }

    public PontoDispensacaoDTO findById(Long id) {
        PontoDispensacao entity = findPontoDispensacaoById(id);
        return presenter.toDto(entity);
    }

    private PontoDispensacao findPontoDispensacaoById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> buildEntityNotFoundException(id));
    }

    private Page<PontoDispensacaoDTO> convertPageToDto(Page<PontoDispensacao> entities) {
        return entities.map(presenter::toDto);
    }

    private IllegalArgumentException buildEntityNotFoundException(Long id) {
        return new IllegalArgumentException("Ponto de Dispensação não encontrado com ID: " + id);
    }
}

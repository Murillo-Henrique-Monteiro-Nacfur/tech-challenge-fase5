package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
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
        return repository.findAll(pageable).map(presenter::toDto);
    }

    public PontoDispensacaoDTO findById(Long id) {
        return repository.findById(id)
                .map(presenter::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Ponto de Dispensação não encontrado com ID: " + id));
    }
}

package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.dto.PontoDispensacaoDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.presenter.PontoDispensacaoPresenter;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.validations.pontodispensacao.PontoDispensacaoCreateValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PontoDispensacaoCreateUseCase {

    private final PontoDispensacaoRepository repository;
    private final List<PontoDispensacaoCreateValidation> validations;
    private final PontoDispensacaoPresenter presenter;

    public PontoDispensacaoDTO execute(PontoDispensacaoDTO dto) {
        validations.forEach(v -> v.validate(dto));
        PontoDispensacao entity = presenter.toEntity(dto);
        entity = repository.save(entity);
        return presenter.toDto(entity);
    }
}

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
        PontoDispensacao existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ponto de Dispensação não encontrado com ID: " + id));

        existing.setCnes(dto.cnes());
        existing.setNome(dto.nome());
        existing.setTipo(dto.tipo());
        existing.setEmailResponsavel(dto.emailResponsavel());
        // clientId geralmente não muda, mas se precisar, adicione aqui

        return presenter.toDto(repository.save(existing));
    }
}

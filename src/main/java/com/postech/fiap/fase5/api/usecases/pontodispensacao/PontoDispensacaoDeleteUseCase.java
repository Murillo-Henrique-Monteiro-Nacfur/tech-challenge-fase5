package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PontoDispensacaoDeleteUseCase {

    private final PontoDispensacaoRepository repository;

    public void execute(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Ponto de Dispensação não encontrado com ID: " + id);
        }
        repository.deleteById(id);
    }
}

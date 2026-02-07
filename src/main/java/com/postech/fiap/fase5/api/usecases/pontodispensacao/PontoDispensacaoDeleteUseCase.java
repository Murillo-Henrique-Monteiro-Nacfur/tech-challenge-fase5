package com.postech.fiap.fase5.api.usecases.pontodispensacao;

import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PontoDispensacaoDeleteUseCase {

    private final PontoDispensacaoRepository repository;

    public void execute(Long id) {
        validateEntityExists(id);
        deletePontoDispensacao(id);
    }

    private void validateEntityExists(Long id) {
        if (!repository.existsById(id)) {
            throw buildEntityNotFoundException(id);
        }
    }

    private void deletePontoDispensacao(Long id) {
        repository.deleteById(id);
    }

    private IllegalArgumentException buildEntityNotFoundException(Long id) {
        return new IllegalArgumentException("Ponto de Dispensação não encontrado com ID: " + id);
    }
}

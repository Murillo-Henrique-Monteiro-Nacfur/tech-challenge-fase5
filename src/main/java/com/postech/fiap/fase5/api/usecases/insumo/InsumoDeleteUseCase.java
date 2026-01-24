package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsumoDeleteUseCase {

    private final InsumoRepository insumoRepository;

    public void execute(Long id) {
        if (!insumoRepository.existsById(id)) {
            throw new IllegalArgumentException("Insumo não encontrado com ID: " + id);
        }
        insumoRepository.deleteById(id);
    }
}

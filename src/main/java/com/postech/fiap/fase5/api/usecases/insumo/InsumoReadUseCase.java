package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsumoReadUseCase {

    private final InsumoRepository insumoRepository;

    public Page<InsumoDTO> findAll(Pageable pageable) {
        return insumoRepository.findAll(pageable).map(InsumoDTO::fromEntity);
    }

    public InsumoDTO findById(Long id) {
        return insumoRepository.findById(id)
                .map(InsumoDTO::fromEntity)
                .orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado com ID: " + id));
    }
}

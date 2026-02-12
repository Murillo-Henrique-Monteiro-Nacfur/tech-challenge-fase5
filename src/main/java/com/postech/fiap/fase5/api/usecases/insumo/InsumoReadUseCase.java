package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.presenter.InsumoPresenter;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import com.postech.fiap.fase5.infrastructure.exceptions.ApplicationNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsumoReadUseCase {

    private final InsumoRepository insumoRepository;
    private final InsumoPresenter insumoPresenter;

    public Page<InsumoDTO> findAll(Pageable pageable) {
        return insumoRepository.findAll(pageable).map(insumoPresenter::toDto);
    }

    public InsumoDTO findById(Long id) {
        return insumoRepository.findById(id)
                .map(insumoPresenter::toDto)
                .orElseThrow(() -> new ApplicationNotFoundException("Insumo não encontrado com ID: " + id));
    }
}

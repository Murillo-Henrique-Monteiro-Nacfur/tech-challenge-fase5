package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;

import com.postech.fiap.fase5.api.validations.insumo.InsumoCreateValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsumoCreateUseCase {

    private final InsumoRepository insumoRepository;
    private final List<InsumoCreateValidation> createValidations;

    public InsumoDTO execute(InsumoDTO insumoDTO) {
        createValidations.forEach(v -> v.validate(insumoDTO));
        Insumo insumo = insumoDTO.toEntity();
        insumo = insumoRepository.save(insumo);
        return InsumoDTO.fromEntity(insumo);
    }
}

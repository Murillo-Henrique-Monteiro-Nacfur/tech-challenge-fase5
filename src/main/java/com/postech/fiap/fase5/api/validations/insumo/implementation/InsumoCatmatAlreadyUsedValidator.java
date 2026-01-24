package com.postech.fiap.fase5.api.validations.implementation;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import com.postech.fiap.fase5.api.validations.InsumoCreateValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InsumoCatmatAlreadyUsedValidator implements InsumoCreateValidation {

    private final InsumoRepository insumoRepository;

    @Override
    public void validate(InsumoDTO insumoDTO) {
        if (insumoDTO.codigoCatmat() != null && insumoRepository.existsByCodigoCatmat(insumoDTO.codigoCatmat())) {
            throw new IllegalArgumentException("Código CATMAT já cadastrado: " + insumoDTO.codigoCatmat());
        }
    }
}

package com.postech.fiap.fase5.api.usecases.insumo;

import com.postech.fiap.fase5.api.dto.insumos.InsumoDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.presenter.InsumoPresenter;
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
    private final InsumoPresenter insumoPresenter;

    public InsumoDTO execute(InsumoDTO insumoDTO) {
        validateInsumoData(insumoDTO);
        Insumo createdInsumo = createInsumo(insumoDTO);
        return insumoPresenter.toDto(createdInsumo);
    }

    private void validateInsumoData(InsumoDTO insumoDTO) {
        createValidations.forEach(validation -> validation.validate(insumoDTO));
    }

    private Insumo createInsumo(InsumoDTO insumoDTO) {
        Insumo insumo = insumoPresenter.toEntity(insumoDTO);
        return insumoRepository.save(insumo);
    }
}

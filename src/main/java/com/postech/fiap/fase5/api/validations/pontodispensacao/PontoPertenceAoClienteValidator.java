package com.postech.fiap.fase5.api.validations.pontodispensacao;

import com.postech.fiap.fase5.api.dto.RegistroConsumoDTO;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.validations.ConsumoValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PontoPertenceAoClienteValidator implements ConsumoValidation {

    private final PontoDispensacaoRepository pontoDispensacaoRepository;

    @Override
    public void validate(RegistroConsumoDTO dto, Long clientId) {
        boolean pertence = pontoDispensacaoRepository.findByCnes(dto.cnesPontoDispensacao()).isPresent();
        if (!pertence) {
            throw new SecurityException("Ponto de Dispensação não pertence ao Cliente autenticado ou não existe.");
        }
    }
}

package com.postech.fiap.fase5.api.validations.implementation;

import com.postech.fiap.fase5.api.dto.ItemConsumoDTO;
import com.postech.fiap.fase5.api.dto.RegistroConsumoDTO;
import com.postech.fiap.fase5.api.validations.ConsumoValidation;
import org.springframework.stereotype.Component;

@Component
public class ConsumoDadosBasicosValidator implements ConsumoValidation {

    @Override
    public void validate(RegistroConsumoDTO dto, Long clientId) {
        if (dto.listaConsumo() == null || dto.listaConsumo().isEmpty()) {
            throw new IllegalArgumentException("A lista de consumo não pode estar vazia.");
        }
        for (ItemConsumoDTO item : dto.listaConsumo()) {
            if (item.quantidadeConsumida() == null || item.quantidadeConsumida() <= 0) {
                throw new IllegalArgumentException("Quantidade consumida deve ser maior que zero.");
            }
        }
    }
}

package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.ItemCargaDTO;
import com.postech.fiap.fase5.api.entities.Insumo;
import com.postech.fiap.fase5.api.entities.Lote;
import com.postech.fiap.fase5.api.repositories.InsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoteCreateUpdateUseCase {

    private final LoteRepository loteRepository;
    private final InsumoRepository insumoRepository;

    public Lote execute(ItemCargaDTO item) {
        Insumo insumo = insumoRepository.findByCodigoCatmat(item.idInsumo())
                .orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado: " + item.idInsumo()));

        return loteRepository.findByNumeroLoteAndInsumoId(item.numeroLote(), insumo.getId())
                .orElseGet(() -> {
                    Lote novoLote = new Lote();
                    novoLote.setNumeroLote(item.numeroLote());
                    novoLote.setInsumo(insumo);
                    novoLote.setDataValidade(item.dataValidade());
                    novoLote.setDataFabricacao(item.dataFabricacao());
                    novoLote.setQuantidade(item.quantidadeTotalLote());
                    return loteRepository.save(novoLote);
                });
    }
}

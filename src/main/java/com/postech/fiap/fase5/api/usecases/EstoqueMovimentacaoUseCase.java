package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.entities.Lote;
import com.postech.fiap.fase5.api.entities.LoteInventario;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EstoqueMovimentacaoUseCase {

    private final LoteInventarioRepository loteInventarioRepository;

    public void execute(PontoDispensacao ponto, Lote lote, Integer quantidadeEnviada) {
        LoteInventario inventario = loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(ponto.getId(), lote.getId())
                .orElseGet(() -> {
                    LoteInventario novo = new LoteInventario();
                    novo.setPontoDispensacao(ponto);
                    novo.setLote(lote);
                    novo.setQuantidade(0);
                    novo.setDataHoraChegada(LocalDateTime.now());
                    return novo;
                });

        // Soma a quantidade recebida ao saldo atual
        inventario.setQuantidade(inventario.getQuantidade() + quantidadeEnviada);
        inventario.setDataHoraChegada(LocalDateTime.now()); // Atualiza data da última movimentação
        loteInventarioRepository.save(inventario);
    }
}

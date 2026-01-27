package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.CargaEstoqueDTO;
import com.postech.fiap.fase5.api.dto.ItemCargaDTO;
import com.postech.fiap.fase5.api.entities.Lote;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.usecases.insumo.InsumoCreateUpdateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProcessarCargaEstoqueUseCase {

    private final PontoDispensacaoRepository pontoDispensacaoRepository;
    private final InsumoCreateUpdateUseCase insumoCreateUpdateUseCase;
    private final LoteCreateUpdateUseCase loteCreateUpdateUseCase;
    private final EstoqueMovimentacaoUseCase estoqueMovimentacaoUseCase;

    @Transactional
    public void execute(CargaEstoqueDTO cargaDTO, Long clientId) {
        // 1. Segurança: Identificar o Ponto de Dispensação pelo Client ID do token
        PontoDispensacao ponto = pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cargaDTO.cnesPontoDispensacao())
                .orElseThrow(() -> new SecurityException("Cliente não possui Ponto de Dispensação vinculado."));

        // 2. Processar Insumos (Garantir cadastro)
        insumoCreateUpdateUseCase.execute(cargaDTO.insumosDetalhes());

        // 3. Processar Itens (Lotes e Estoque)
        if (cargaDTO.itens() != null) {
            for (ItemCargaDTO item : cargaDTO.itens()) {
                // Garante/Cria o Lote Mestre
                Lote lote = loteCreateUpdateUseCase.execute(item);

                // Atualiza o Estoque no Ponto
                estoqueMovimentacaoUseCase.execute(ponto, lote, item.quantidadeEnviada());
            }
        }
    }
}

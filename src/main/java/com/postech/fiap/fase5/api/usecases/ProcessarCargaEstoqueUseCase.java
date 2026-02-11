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

import java.util.List;

@Component
@RequiredArgsConstructor
public class ProcessarCargaEstoqueUseCase {

    private final PontoDispensacaoRepository pontoDispensacaoRepository;
    private final InsumoCreateUpdateUseCase insumoCreateUpdateUseCase;
    private final LoteCreateUpdateUseCase loteCreateUpdateUseCase;
    private final EstoqueMovimentacaoUseCase estoqueMovimentacaoUseCase;

    @Transactional
    public void execute(CargaEstoqueDTO cargaDTO, Long clientId) {
        PontoDispensacao ponto = buscarPontoDispensacao(clientId, cargaDTO.cnesPontoDispensacao());

        processarInsumos(cargaDTO.insumosDetalhes());

        processarItensCarga(cargaDTO.itens(), ponto);
    }

    private PontoDispensacao buscarPontoDispensacao(Long clientId, String cnes) {
        return pontoDispensacaoRepository.findByClientIdAndCnes(clientId, cnes)
                .orElseThrow(() -> new SecurityException("Cliente não possui Ponto de Dispensação vinculado."));
    }

    private void processarInsumos(List<com.postech.fiap.fase5.api.dto.insumos.InsumoDetalheDTO> insumosDetalhes) {
        insumoCreateUpdateUseCase.execute(insumosDetalhes);
    }

    private void processarItensCarga(List<ItemCargaDTO> itens, PontoDispensacao ponto) {
        if (hasItens(itens)) {
            itens.forEach(item -> processarItemCarga(item, ponto));
        }
    }

    private boolean hasItens(List<ItemCargaDTO> itens) {
        return itens != null && !itens.isEmpty();
    }

    private void processarItemCarga(ItemCargaDTO item, PontoDispensacao ponto) {
        Lote lote = loteCreateUpdateUseCase.execute(item);
        estoqueMovimentacaoUseCase.execute(ponto, lote, item.quantidadeEnviada());
    }
}

package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.ItemConsumoDTO;
import com.postech.fiap.fase5.api.dto.RegistroConsumoDTO;
import com.postech.fiap.fase5.api.entities.HistoricoConsumo;
import com.postech.fiap.fase5.api.entities.LoteInventario;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.HistoricoConsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.validations.ConsumoValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RegistrarConsumoUseCase {

    private final LoteInventarioRepository loteInventarioRepository;
    private final HistoricoConsumoRepository historicoConsumoRepository;
    private final PontoDispensacaoRepository pontoDispensacaoRepository;
    private final List<ConsumoValidation> validations;

    @Transactional
    public void execute(RegistroConsumoDTO registroConsumo, Long clientId) {
        executeValidations(registroConsumo, clientId);
        PontoDispensacao pontoDispensacao = findPontoDispensacao(registroConsumo.pontoDispensacaoId());
        processarItensConsumo(registroConsumo.listaConsumo(), pontoDispensacao);
    }

    private void executeValidations(RegistroConsumoDTO registroConsumo, Long clientId) {
        validations.forEach(validation -> validation.validate(registroConsumo, clientId));
    }

    private PontoDispensacao findPontoDispensacao(Long pontoDispensacaoId) {
        return pontoDispensacaoRepository.findById(pontoDispensacaoId)
                .orElseThrow(() -> new IllegalArgumentException("Ponto de Dispensação não encontrado."));
    }

    private void processarItensConsumo(List<ItemConsumoDTO> itensConsumo, PontoDispensacao pontoDispensacao) {
        itensConsumo.forEach(itemConsumo -> processarItemConsumo(itemConsumo, pontoDispensacao));
    }

    private void processarItemConsumo(ItemConsumoDTO itemConsumo, PontoDispensacao pontoDispensacao) {
        LoteInventario loteInventario = findLoteInventario(pontoDispensacao.getId(), itemConsumo.loteId());
        validarSaldoDisponivel(loteInventario, itemConsumo);
        atualizarQuantidadeInventario(loteInventario, itemConsumo.quantidadeConsumida());
        registrarHistoricoConsumo(pontoDispensacao, loteInventario, itemConsumo);
    }

    private LoteInventario findLoteInventario(Long pontoDispensacaoId, Long loteId) {
        return loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(pontoDispensacaoId, loteId)
                .orElseThrow(() -> new IllegalArgumentException("Lote ID " + loteId + " não encontrado no inventário deste ponto."));
    }

    private void validarSaldoDisponivel(LoteInventario loteInventario, ItemConsumoDTO itemConsumo) {
        boolean saldoInsuficiente = loteInventario.getQuantidade() < itemConsumo.quantidadeConsumida();
        if (saldoInsuficiente) {
            throw new IllegalArgumentException("Saldo insuficiente para o lote " + itemConsumo.loteId() + ". Disponível: " + loteInventario.getQuantidade());
        }
    }

    private void atualizarQuantidadeInventario(LoteInventario loteInventario, Integer quantidadeConsumida) {
        int novaQuantidade = loteInventario.getQuantidade() - quantidadeConsumida;
        loteInventario.setQuantidade(novaQuantidade);
        loteInventarioRepository.save(loteInventario);
    }

    private void registrarHistoricoConsumo(PontoDispensacao pontoDispensacao,
                                           LoteInventario loteInventario,
                                           ItemConsumoDTO itemConsumo) {
        HistoricoConsumo historicoConsumo = criarHistoricoConsumo(pontoDispensacao, loteInventario, itemConsumo);
        historicoConsumoRepository.save(historicoConsumo);
    }

    private HistoricoConsumo criarHistoricoConsumo(PontoDispensacao pontoDispensacao,
                                                   LoteInventario loteInventario,
                                                   ItemConsumoDTO itemConsumo) {
        HistoricoConsumo historicoConsumo = new HistoricoConsumo();
        historicoConsumo.setPontoDispensacao(pontoDispensacao);
        historicoConsumo.setLote(loteInventario.getLote());
        historicoConsumo.setQuantidade(itemConsumo.quantidadeConsumida());
        historicoConsumo.setDataHora(itemConsumo.dataHoraEvento());
        return historicoConsumo;
    }
}

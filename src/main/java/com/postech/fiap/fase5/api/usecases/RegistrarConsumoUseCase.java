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
    public void execute(RegistroConsumoDTO dto, Long clientId) {
        // 1. Executar validações prévias (Segurança, Dados básicos)
        validations.forEach(v -> v.validate(dto, clientId));

        // Recuperar o Ponto (já validado que existe e pertence ao cliente)
        PontoDispensacao ponto = pontoDispensacaoRepository.findById(dto.pontoDispensacaoId())
                .orElseThrow(() -> new IllegalArgumentException("Ponto de Dispensação não encontrado."));

        // 2. Processar cada item
        for (ItemConsumoDTO item : dto.listaConsumo()) {
            // Buscar Lote no Inventário
            LoteInventario inventario = loteInventarioRepository.findByPontoDispensacaoIdAndLoteId(ponto.getId(), item.loteId())
                    .orElseThrow(() -> new IllegalArgumentException("Lote ID " + item.loteId() + " não encontrado no inventário deste ponto."));

            // Validar Saldo
            if (inventario.getQuantidade() < item.quantidadeConsumida()) {
                throw new IllegalArgumentException("Saldo insuficiente para o lote " + item.loteId() + ". Disponível: " + inventario.getQuantidade());
            }

            // Atualizar Inventário
            inventario.setQuantidade(inventario.getQuantidade() - item.quantidadeConsumida());
            loteInventarioRepository.save(inventario);

            // Registrar Histórico
            HistoricoConsumo historico = new HistoricoConsumo();
            historico.setPontoDispensacao(ponto);
            historico.setLote(inventario.getLote());
            historico.setQuantidade(item.quantidadeConsumida());
            historico.setDataHora(item.dataHoraEvento());
            // historico.setIdUser(???); // Se tiver usuário logado no sistema da farmácia, poderia vir no DTO. Por enquanto null ou do token se fosse user pessoa.
            
            historicoConsumoRepository.save(historico);
        }
    }
}

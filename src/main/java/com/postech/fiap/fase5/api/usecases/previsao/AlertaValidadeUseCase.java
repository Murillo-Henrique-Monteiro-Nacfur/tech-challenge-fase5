package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.AlertaValidadeDTO;
import com.postech.fiap.fase5.api.dto.estimativa.ItemRiscoValidadeDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.HistoricoConsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.services.NotificacaoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AlertaValidadeUseCase {

    private final PontoDispensacaoRepository pontoDispensacaoRepository;
    private final LoteInventarioRepository loteInventarioRepository;
    private final HistoricoConsumoRepository historicoConsumoRepository;
    private final NotificacaoService notificacaoService;

    public List<AlertaValidadeDTO> execute() {
        log.info("Iniciando verificação de risco de validade");

        // 1. Buscas em Banco
        List<PontoDispensacao> pontos = pontoDispensacaoRepository.findAll();
        List<LoteInventarioProjection> todosLotes = loteInventarioRepository.findAllLotePorInventario();
        
        // Buscamos histórico para calcular a média de consumo (base para saber se vai sobrar)
        List<HistoricoConsumoPorDiaProjection> historicoRecente = historicoConsumoRepository
                .buscaHistoricoPorDiaNosUltimosTrintaDias(LocalDateTime.now().minusDays(30), LocalDateTime.now().plusDays(1));

        // 2. Agrupamentos para otimização
        Map<Long, List<LoteInventarioProjection>> lotesPorPonto = todosLotes.stream()
                .collect(Collectors.groupingBy(LoteInventarioProjection::getIdPontoDispensacao));

        // Mapa: PontoID -> InsumoID -> TotalConsumo30Dias
        Map<Long, Map<Long, Long>> consumoPorPontoEInsumo = agruparConsumo(historicoRecente);

        List<AlertaValidadeDTO> alertas = new ArrayList<>();

        // 3. Processamento por Ponto
        for (PontoDispensacao ponto : pontos) {
            List<LoteInventarioProjection> lotesDoPonto = lotesPorPonto.getOrDefault(ponto.getId(), Collections.emptyList());
            Map<Long, Long> consumoDoPonto = consumoPorPontoEInsumo.getOrDefault(ponto.getId(), Collections.emptyMap());

            List<ItemRiscoValidadeDTO> itensEmRisco = processarLotesDoPonto(lotesDoPonto, consumoDoPonto);

            if (!itensEmRisco.isEmpty()) {
                alertas.add(AlertaValidadeDTO.builder()
                        .pontoDispensacao(ponto)
                        .itensEmRisco(itensEmRisco)
                        .build());
            }
        }

        // 4. Notificação
        notificacaoService.notificarRiscoValidade(alertas);

        return alertas;
    }

    private List<ItemRiscoValidadeDTO> processarLotesDoPonto(List<LoteInventarioProjection> lotes, Map<Long, Long> consumoDoPonto) {
        List<ItemRiscoValidadeDTO> riscos = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        LocalDate limiteAlerta = hoje.plusDays(60); // Olhamos 60 dias para frente

        for (LoteInventarioProjection lote : lotes) {
            LocalDate validade = lote.getDataValidade();
            
            // Filtro 1: Está na janela de risco (Vence nos próximos 60 dias)?
            if (validade != null && !validade.isBefore(hoje) && validade.isBefore(limiteAlerta)) {
                
                long diasParaVencer = ChronoUnit.DAYS.between(hoje, validade);
                if (diasParaVencer <= 0) diasParaVencer = 1; // Evita divisão por zero ou lógica estranha se vencer hoje

                // Cálculo da Média Diária
                Long consumoTotal30Dias = consumoDoPonto.getOrDefault(lote.getIdInsumo(), 0L);
                double mediaDiaria = consumoTotal30Dias / 30.0;

                // Filtro 2: Vai sobrar? (Inteligência)
                // Consumo Previsto até o vencimento = Media * DiasRestantes
                double consumoPrevistoAteVencimento = mediaDiaria * diasParaVencer;
                
                // Sobra = EstoqueAtual - ConsumoPrevisto
                int sobraPrevista = (int) (lote.getQuantidade() - consumoPrevistoAteVencimento);

                if (sobraPrevista > 0) {
                    // Risco Confirmado: Vai vencer produto na prateleira
                    riscos.add(ItemRiscoValidadeDTO.builder()
                            .idInsumo(lote.getIdInsumo())
                            .nomeInsumo(lote.getNomeInsumo())
                            .numeroLote(lote.getNumeroLote())
                            .dataValidade(validade)
                            .quantidadeAtual(lote.getQuantidade())
                            .consumoMedioDiario(mediaDiaria)
                            .diasParaVencer(diasParaVencer)
                            .quantidadeDesperdicioPrevisto(sobraPrevista)
                            .build());
                }
            }
        }
        return riscos;
    }

    private Map<Long, Map<Long, Long>> agruparConsumo(List<HistoricoConsumoPorDiaProjection> historico) {
        // Retorna Map<PontoId, Map<InsumoId, TotalConsumo>>
        return historico.stream()
                .collect(Collectors.groupingBy(
                        HistoricoConsumoPorDiaProjection::getIdPontoDispensacao,
                        Collectors.groupingBy(
                                HistoricoConsumoPorDiaProjection::getIdInsumo,
                                Collectors.summingLong(h -> h.getTotalConsumo() != null ? h.getTotalConsumo() : 0)
                        )
                ));
    }
}

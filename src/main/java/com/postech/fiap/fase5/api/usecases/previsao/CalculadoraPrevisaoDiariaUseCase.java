package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculadoraPrevisaoDiariaUseCase {

    // --- MÉTODOS PÚBLICOS (Pontos de Entrada) ---

    public List<InventarioDiarioDTO> execute(List<InventarioDiarioDTO> dadosBrutos) {
        // 1. Calcula métricas individuais
        dadosBrutos.forEach(this::processarPrevisaoDiariaPonto);

        // 2. Identifica oportunidades de transferência (Cross-check)
        gerarSugestoesTransferenciaDiaria(dadosBrutos);

        return dadosBrutos;
    }

    // --- LÓGICA DE CÁLCULO INDIVIDUAL ---

    private void processarPrevisaoDiariaPonto(InventarioDiarioDTO pontoDTO) {
        List<HistoricoConsumoPorDiaDTO> historicoConsumo = pontoDTO.getHistoricoConsumo();
        LocalDate dataLimite = LocalDate.now().minusDays(30);

        for (InsumoDiarioDTO insumoDTO : pontoDTO.getInsumos()) {
            List<HistoricoConsumoPorDiaDTO> historicoRecente = historicoConsumo.stream()
                    .filter(h -> h.getDia() != null && h.getDia().isAfter(dataLimite) && h.getDia().isBefore(LocalDate.now().plusDays(1)))
                    .toList();

            calcularPrevisaoRecente(insumoDTO, historicoRecente);
        }
    }

    private void calcularPrevisaoRecente(InsumoDiarioDTO insumoDTO, List<HistoricoConsumoPorDiaDTO> historicoTotal) {
        List<HistoricoConsumoPorDiaDTO> historicoInsumo = historicoTotal.stream()
                .filter(h -> h.getIdInsumo().equals(insumoDTO.getIdInsumo()))
                .toList();

        if (historicoInsumo.isEmpty()) {
            insumoDTO.setConsumoMedioDiario(0.0);
            insumoDTO.setPrevisaoEsgotamentoDias(999);
            insumoDTO.setStatusPrevisao("SEM_DADOS_HISTORICOS");
            return;
        }

        long totalConsumido = historicoInsumo.stream()
                .mapToLong(e -> e.getTotalConsumo() != null ? e.getTotalConsumo() : 0)
                .sum();
        
        double mediaDiaria = (double) totalConsumido / 30.0;
        
        insumoDTO.setConsumoMedioDiario(mediaDiaria);
        
        // Lógica de Status
        int diasRestantes;
        String status;
        if (mediaDiaria <= 0) {
            diasRestantes = 999;
            status = "SEM_CONSUMO_PREVISTO";
        } else {
            diasRestantes = (int) (insumoDTO.getQuantidade() / mediaDiaria);
            if (diasRestantes <= 5) status = "CRITICO";
            else if (diasRestantes <= 15) status = "ALERTA";
            else status = "NORMAL";
        }
        
        insumoDTO.setPrevisaoEsgotamentoDias(diasRestantes);
        insumoDTO.setStatusPrevisao(status);
    }

    // --- LÓGICA DE TRANSFERÊNCIA (CROSS-CHECK) ---

    private void gerarSugestoesTransferenciaDiaria(List<InventarioDiarioDTO> todosPontos) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = new HashMap<>();

        // 1. Identificar Doadores
        for (InventarioDiarioDTO ponto : todosPontos) {
            for (InsumoDiarioDTO insumo : ponto.getInsumos()) {
                // Regra: Só doa se tiver estoque para mais de 30 dias
                if (insumo.getPrevisaoEsgotamentoDias() != null && insumo.getPrevisaoEsgotamentoDias() > 30) {
                    
                    // 1. Calcula Estoque Transferível (Lotes com validade > 30 dias)
                    int estoqueTransferivel = calcularEstoqueTransferivel(insumo.getLotes());

                    // 2. Cálculo do Excedente Seguro: EstoqueTransferivel - (ConsumoDiario * 30)
                    double consumo30Dias = (insumo.getConsumoMedioDiario() != null ? insumo.getConsumoMedioDiario() : 0.0) * 30.0;
                    int excedente = (int) (estoqueTransferivel - consumo30Dias);

                    if (excedente > 0) {
                        SugestaoTransferenciaDTO doador = SugestaoTransferenciaDTO.builder()
                                .idPontoDoador(ponto.getPontoDispensacao().getId())
                                .nomePontoDoador(ponto.getPontoDispensacao().getNome())
                                .quantidadeDisponivelNoDoador(excedente) // Agora mostra apenas o que pode ser doado (válido e excedente)
                                .previsaoDiasDoador(insumo.getPrevisaoEsgotamentoDias())
                                .build();
                        mapaDoadores.computeIfAbsent(insumo.getIdInsumo(), k -> new ArrayList<>()).add(doador);
                    }
                }
            }
        }

        // 2. Atribuir Sugestões
        for (InventarioDiarioDTO ponto : todosPontos) {
            for (InsumoDiarioDTO insumo : ponto.getInsumos()) {
                if ("CRITICO".equals(insumo.getStatusPrevisao()) || "ALERTA".equals(insumo.getStatusPrevisao())) {
                    List<SugestaoTransferenciaDTO> doadores = mapaDoadores.getOrDefault(insumo.getIdInsumo(), new ArrayList<>());
                    List<SugestaoTransferenciaDTO> validos = doadores.stream()
                            .filter(d -> !d.getIdPontoDoador().equals(ponto.getPontoDispensacao().getId()))
                            .toList();
                    insumo.setSugestoesTransferencia(validos);
                }
            }
        }
    }

    private int calcularEstoqueTransferivel(List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes) {
        if (lotes == null || lotes.isEmpty()) return 0;
        
        LocalDate dataCorte = LocalDate.now().plusDays(30);
        
        return lotes.stream()
                .filter(lote -> lote.getDataValidade() != null && lote.getDataValidade().isAfter(dataCorte))
                .mapToInt(InventarioPontoDispensacaoInsumosPorLoteDTO::getQuantidade)
                .sum();
    }
}

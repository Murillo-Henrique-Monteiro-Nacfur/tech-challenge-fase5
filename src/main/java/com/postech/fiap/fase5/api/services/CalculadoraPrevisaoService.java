package com.postech.fiap.fase5.api.services;

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
public class CalculadoraPrevisaoService {

    // --- MÉTODOS PÚBLICOS (Pontos de Entrada) ---

    public List<InventarioDiarioDTO> calcularPrevisaoDiaria(List<InventarioDiarioDTO> dadosBrutos) {
        // 1. Calcula métricas individuais
        dadosBrutos.forEach(this::processarPrevisaoDiariaPonto);

        // 2. Identifica oportunidades de transferência (Cross-check)
        gerarSugestoesTransferenciaDiaria(dadosBrutos);

        return dadosBrutos;
    }

    public List<InventarioMensalDTO> calcularPrevisaoMensal(List<InventarioMensalDTO> dadosBrutos) {
        // 1. Calcula métricas individuais
        dadosBrutos.forEach(this::processarPrevisaoMensalPonto);

        // 2. Identifica oportunidades de transferência (Cross-check)
        gerarSugestoesTransferenciaMensal(dadosBrutos);

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

    private void processarPrevisaoMensalPonto(InventarioMensalDTO pontoDTO) {
        List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal = pontoDTO.getHistoricoSazonal();

        for (InsumoMensalDTO insumoDTO : pontoDTO.getInsumos()) {
            calcularPrevisaoSazonal(insumoDTO, historicoSazonal);
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

    private void calcularPrevisaoSazonal(InsumoMensalDTO insumoDTO, List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal) {
        if (historicoSazonal == null || historicoSazonal.isEmpty()) {
            definirSemDadosSazonais(insumoDTO);
            return;
        }

        List<HistoricoConsumoMesAnosAnterioresDTO> historicoInsumo = historicoSazonal.stream()
                .filter(h -> h.getIdInsumo().equals(insumoDTO.getIdInsumo()))
                .toList();

        if (historicoInsumo.isEmpty()) {
            definirSemDadosSazonais(insumoDTO);
            return;
        }

        long totalConsumoAnos = historicoInsumo.stream()
                .mapToLong(HistoricoConsumoMesAnosAnterioresDTO::getTotalConsumo)
                .sum();

        int numeroDeAnos = historicoInsumo.size();
        double mediaMensalSazonal = (double) totalConsumoAnos / (numeroDeAnos > 0 ? numeroDeAnos : 1);
        double mediaDiariaSazonal = mediaMensalSazonal / 30.0;

        insumoDTO.setConsumoMedioDiarioSazonal(mediaDiariaSazonal);
        
        // Lógica de Status
        int diasRestantes;
        String status;
        if (mediaDiariaSazonal <= 0) {
            diasRestantes = 999;
            status = "SEM_CONSUMO_PREVISTO";
        } else {
            diasRestantes = (int) (insumoDTO.getQuantidade() / mediaDiariaSazonal);
            if (diasRestantes <= 5) status = "CRITICO";
            else if (diasRestantes <= 15) status = "ALERTA";
            else status = "NORMAL";
        }

        insumoDTO.setPrevisaoEsgotamentoDiasSazonal(diasRestantes);
        insumoDTO.setStatusPrevisaoSazonal(status);
    }

    private void definirSemDadosSazonais(InsumoMensalDTO insumoDTO) {
        insumoDTO.setConsumoMedioDiarioSazonal(0.0);
        insumoDTO.setPrevisaoEsgotamentoDiasSazonal(999);
        insumoDTO.setStatusPrevisaoSazonal("SEM_DADOS_SAZONAIS");
    }

    // --- LÓGICA DE TRANSFERÊNCIA (CROSS-CHECK) ---

    private void gerarSugestoesTransferenciaDiaria(List<InventarioDiarioDTO> todosPontos) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = new HashMap<>();

        // 1. Identificar Doadores
        for (InventarioDiarioDTO ponto : todosPontos) {
            for (InsumoDiarioDTO insumo : ponto.getInsumos()) {
                if (insumo.getPrevisaoEsgotamentoDias() != null && insumo.getPrevisaoEsgotamentoDias() > 30) {
                    SugestaoTransferenciaDTO doador = SugestaoTransferenciaDTO.builder()
                            .idPontoDoador(ponto.getPontoDispensacao().getId())
                            .nomePontoDoador(ponto.getPontoDispensacao().getNome())
                            .quantidadeDisponivelNoDoador(insumo.getQuantidade())
                            .previsaoDiasDoador(insumo.getPrevisaoEsgotamentoDias())
                            .build();
                    mapaDoadores.computeIfAbsent(insumo.getIdInsumo(), k -> new ArrayList<>()).add(doador);
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

    private void gerarSugestoesTransferenciaMensal(List<InventarioMensalDTO> todosPontos) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = new HashMap<>();

        // 1. Identificar Doadores
        for (InventarioMensalDTO ponto : todosPontos) {
            for (InsumoMensalDTO insumo : ponto.getInsumos()) {
                if (insumo.getPrevisaoEsgotamentoDiasSazonal() != null && insumo.getPrevisaoEsgotamentoDiasSazonal() > 30) {
                    SugestaoTransferenciaDTO doador = SugestaoTransferenciaDTO.builder()
                            .idPontoDoador(ponto.getPontoDispensacao().getId())
                            .nomePontoDoador(ponto.getPontoDispensacao().getNome())
                            .quantidadeDisponivelNoDoador(insumo.getQuantidade())
                            .previsaoDiasDoador(insumo.getPrevisaoEsgotamentoDiasSazonal())
                            .build();
                    mapaDoadores.computeIfAbsent(insumo.getIdInsumo(), k -> new ArrayList<>()).add(doador);
                }
            }
        }

        // 2. Atribuir Sugestões
        for (InventarioMensalDTO ponto : todosPontos) {
            for (InsumoMensalDTO insumo : ponto.getInsumos()) {
                if ("CRITICO".equals(insumo.getStatusPrevisaoSazonal()) || "ALERTA".equals(insumo.getStatusPrevisaoSazonal())) {
                    List<SugestaoTransferenciaDTO> doadores = mapaDoadores.getOrDefault(insumo.getIdInsumo(), new ArrayList<>());
                    List<SugestaoTransferenciaDTO> validos = doadores.stream()
                            .filter(d -> !d.getIdPontoDoador().equals(ponto.getPontoDispensacao().getId()))
                            .toList();
                    insumo.setSugestoesTransferenciaSazonal(validos);
                }
            }
        }
    }
}

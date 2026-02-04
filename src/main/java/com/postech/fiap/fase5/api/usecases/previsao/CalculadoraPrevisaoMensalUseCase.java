package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class CalculadoraPrevisaoMensalUseCase {

    public List<InventarioMensalDTO> execute(List<InventarioMensalDTO> dadosBrutos) {
        dadosBrutos.forEach(this::processarPrevisaoMensalPonto);

        gerarSugestoesTransferenciaMensal(dadosBrutos);

        return dadosBrutos;
    }

    private void processarPrevisaoMensalPonto(InventarioMensalDTO pontoDTO) {
        List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal = pontoDTO.getHistoricoSazonal();

        for (InsumoMensalDTO insumoDTO : pontoDTO.getInsumos()) {
            calcularPrevisaoSazonal(insumoDTO, historicoSazonal);
        }
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

    private void gerarSugestoesTransferenciaMensal(List<InventarioMensalDTO> todosPontos) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = new HashMap<>();

        // 1. Identificar Doadores
        for (InventarioMensalDTO ponto : todosPontos) {
            for (InsumoMensalDTO insumo : ponto.getInsumos()) {
                // Regra: Só doa se tiver estoque para mais de 30 dias
                if (insumo.getPrevisaoEsgotamentoDiasSazonal() != null && insumo.getPrevisaoEsgotamentoDiasSazonal() > 30) {

                    double consumo30Dias = (insumo.getConsumoMedioDiarioSazonal() != null ? insumo.getConsumoMedioDiarioSazonal() : 0.0) * 30.0;
                    int excedente = (int) (insumo.getQuantidade() - consumo30Dias);

                    if (excedente > 0) {
                        SugestaoTransferenciaDTO doador = SugestaoTransferenciaDTO.builder()
                                .idPontoDoador(ponto.getPontoDispensacao().getId())
                                .nomePontoDoador(ponto.getPontoDispensacao().getNome())
                                .quantidadeDisponivelNoDoador(excedente) // Agora mostra apenas o que pode ser doado
                                .previsaoDiasDoador(insumo.getPrevisaoEsgotamentoDiasSazonal())
                                .build();
                        mapaDoadores.computeIfAbsent(insumo.getIdInsumo(), k -> new ArrayList<>()).add(doador);
                    }
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

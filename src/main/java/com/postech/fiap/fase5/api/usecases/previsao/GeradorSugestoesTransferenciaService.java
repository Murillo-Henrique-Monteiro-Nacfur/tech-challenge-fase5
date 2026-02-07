package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GeradorSugestoesTransferenciaService {
    private static final int DIAS_MINIMO_DOADOR = 30;
    private final CalculadorEstoqueService calculadorEstoqueService;
    private final AtribuidorSugestoesService atribuidorSugestoesService;

    public void gerarSugestoes(List<InventarioMensalDTO> inventarios) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = identificarDoadores(inventarios);
        atribuirSugestoesAosReceptores(inventarios, mapaDoadores);
    }

    private Map<Long, List<SugestaoTransferenciaDTO>> identificarDoadores(List<InventarioMensalDTO> inventarios) {
        Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores = new HashMap<>();
        for (InventarioMensalDTO ponto : inventarios) {
            for (InsumoMensalDTO insumo : ponto.getInsumos()) {
                processarPotencialDoador(ponto, insumo, mapaDoadores);
            }
        }
        return mapaDoadores;
    }

    private void processarPotencialDoador(
            InventarioMensalDTO ponto,
            InsumoMensalDTO insumo,
            Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores) {
        if (!isPotencialDoador(insumo)) {
            return;
        }
        int estoqueTransferivel = calculadorEstoqueService.calcularEstoqueTransferivel(insumo.getLotes());
        int excedente = calcularExcedente(insumo, estoqueTransferivel);
        if (excedente > 0) {
            SugestaoTransferenciaDTO sugestao = criarSugestaoDoador(ponto, insumo, excedente);
            mapaDoadores.computeIfAbsent(insumo.getIdInsumo(), k -> new ArrayList<>()).add(sugestao);
        }
    }

    private boolean isPotencialDoador(InsumoMensalDTO insumo) {
        return insumo.getPrevisaoEsgotamentoDiasSazonal() != null
                && insumo.getPrevisaoEsgotamentoDiasSazonal() > DIAS_MINIMO_DOADOR;
    }

    private int calcularExcedente(InsumoMensalDTO insumo, int estoqueTransferivel) {
        double consumo30Dias = obterConsumo30Dias(insumo);
        return (int) (estoqueTransferivel - consumo30Dias);
    }

    private double obterConsumo30Dias(InsumoMensalDTO insumo) {
        double consumoDiario = insumo.getConsumoMedioDiarioSazonal() != null
                ? insumo.getConsumoMedioDiarioSazonal()
                : 0.0;
        return consumoDiario * DIAS_MINIMO_DOADOR;
    }

    private SugestaoTransferenciaDTO criarSugestaoDoador(
            InventarioMensalDTO ponto,
            InsumoMensalDTO insumo,
            int excedente) {
        return SugestaoTransferenciaDTO.builder()
                .idPontoDoador(ponto.getPontoDispensacao().getId())
                .nomePontoDoador(ponto.getPontoDispensacao().getNome())
                .quantidadeDisponivelNoDoador(excedente)
                .previsaoDiasDoador(insumo.getPrevisaoEsgotamentoDiasSazonal())
                .build();
    }

    private void atribuirSugestoesAosReceptores(
            List<InventarioMensalDTO> inventarios,
            Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores) {
        for (InventarioMensalDTO ponto : inventarios) {
            for (InsumoMensalDTO insumo : ponto.getInsumos()) {
                atribuidorSugestoesService.atribuirSugestoes(ponto, insumo, mapaDoadores);
            }
        }
    }
}



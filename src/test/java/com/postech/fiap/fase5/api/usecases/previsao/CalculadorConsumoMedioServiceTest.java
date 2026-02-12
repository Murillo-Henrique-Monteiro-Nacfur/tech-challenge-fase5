package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadorConsumoMedioServiceTest {

    private CalculadorConsumoMedioService calculadorConsumoMedioService;

    @BeforeEach
    void setUp() {
        calculadorConsumoMedioService = new CalculadorConsumoMedioService();
    }

    @Test
    void calcularMediaDiariaDeveRetornarZeroQuandoHistoricoVazio() {
        double resultado = calculadorConsumoMedioService.calcularMediaDiaria(Collections.emptyList());

        assertThat(resultado).isZero();
    }

    @Test
    void calcularMediaDiariaDeveRetornarMediaCorretaComUmAnoDeHistorico() {
        List<HistoricoConsumoMesAnosAnterioresDTO> historico = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder().totalConsumo(300).build()
        );

        double resultado = calculadorConsumoMedioService.calcularMediaDiaria(historico);

        assertThat(resultado).isEqualTo(10.0);
    }

    @Test
    void calcularMediaDiariaDeveRetornarMediaCorretaComMultiplosAnosDeHistorico() {
        List<HistoricoConsumoMesAnosAnterioresDTO> historico = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder().totalConsumo(300).build(),
                HistoricoConsumoMesAnosAnterioresDTO.builder().totalConsumo(600).build()
        );

        double resultado = calculadorConsumoMedioService.calcularMediaDiaria(historico);

        assertThat(resultado).isEqualTo(15.0);
    }

    @Test
    void calcularMediaDiariaDeveRetornarZeroQuandoConsumoTotalZero() {
        List<HistoricoConsumoMesAnosAnterioresDTO> historico = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder().totalConsumo(0).build()
        );

        double resultado = calculadorConsumoMedioService.calcularMediaDiaria(historico);

        assertThat(resultado).isZero();
    }
}


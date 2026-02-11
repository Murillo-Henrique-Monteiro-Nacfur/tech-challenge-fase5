package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InventarioPontoDispensacaoInsumosPorLoteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CalculadorEstoqueServiceTest {

    private CalculadorEstoqueService calculadorEstoqueService;

    @BeforeEach
    void setUp() {
        calculadorEstoqueService = new CalculadorEstoqueService();
    }

    @Test
    void calcularEstoqueValidoDeveRetornarZeroQuandoListaVazia() {
        int resultado = calculadorEstoqueService.calcularEstoqueValido(Collections.emptyList());

        assertThat(resultado).isZero();
    }

    @Test
    void calcularEstoqueValidoDeveRetornarZeroQuandoListaNula() {
        int resultado = calculadorEstoqueService.calcularEstoqueValido(null);

        assertThat(resultado).isZero();
    }

    @Test
    void calcularEstoqueValidoDeveRetornarSomaQuandoTodosLotesSaoValidos() {
        LocalDate dataFutura = LocalDate.now().plusDays(60);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(dataFutura)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataFutura)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueValido(lotes);

        assertThat(resultado).isEqualTo(300);
    }

    @Test
    void calcularEstoqueValidoDeveIgnorarLotesVencidos() {
        LocalDate dataPassada = LocalDate.now().minusDays(10);
        LocalDate dataFutura = LocalDate.now().plusDays(60);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(dataPassada)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataFutura)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueValido(lotes);

        assertThat(resultado).isEqualTo(200);
    }

    @Test
    void calcularEstoqueValidoDeveIgnorarLotesComDataValidadeNula() {
        LocalDate dataFutura = LocalDate.now().plusDays(60);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(null)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataFutura)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueValido(lotes);

        assertThat(resultado).isEqualTo(200);
    }

    @Test
    void calcularEstoqueValidoDeveRetornarZeroQuandoTodosLotesVencidos() {
        LocalDate dataPassada = LocalDate.now().minusDays(10);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(dataPassada)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataPassada)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueValido(lotes);

        assertThat(resultado).isZero();
    }

    @Test
    void calcularEstoqueTransferivelDeveRetornarZeroQuandoListaVazia() {
        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(Collections.emptyList());

        assertThat(resultado).isZero();
    }

    @Test
    void calcularEstoqueTransferivelDeveRetornarZeroQuandoListaNula() {
        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(null);

        assertThat(resultado).isZero();
    }

    @Test
    void calcularEstoqueTransferivelDeveRetornarSomaQuandoTodosLotesSaoTransferiveis() {
        LocalDate dataAlemDaMargem = LocalDate.now().plusDays(60);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(dataAlemDaMargem)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataAlemDaMargem)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(lotes);

        assertThat(resultado).isEqualTo(300);
    }

    @Test
    void calcularEstoqueTransferivelDeveIgnorarLotesDentroMargemTransferencia() {
        LocalDate dataAlemDaMargem = LocalDate.now().plusDays(60);
        LocalDate dataDentroMargem = LocalDate.now().plusDays(20);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(dataDentroMargem)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataAlemDaMargem)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(lotes);

        assertThat(resultado).isEqualTo(200);
    }

    @Test
    void calcularEstoqueTransferivelDeveIgnorarLotesComDataValidadeNula() {
        LocalDate dataAlemDaMargem = LocalDate.now().plusDays(60);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(null)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataAlemDaMargem)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(lotes);

        assertThat(resultado).isEqualTo(200);
    }

    @Test
    void calcularEstoqueTransferivelDeveRetornarZeroQuandoTodosLotesDentroMargem() {
        LocalDate dataDentroMargem = LocalDate.now().plusDays(20);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(dataDentroMargem)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataDentroMargem)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(lotes);

        assertThat(resultado).isZero();
    }

    @Test
    void calcularEstoqueTransferivelDeveIgnorarLotesVencidos() {
        LocalDate dataPassada = LocalDate.now().minusDays(10);
        LocalDate dataAlemDaMargem = LocalDate.now().plusDays(60);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(100)
                        .dataValidade(dataPassada)
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(200)
                        .dataValidade(dataAlemDaMargem)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(lotes);

        assertThat(resultado).isEqualTo(200);
    }

    @Test
    void calcularEstoqueTransferivelDeveConsiderarLoteExatamenteNaDataCorte() {
        LocalDate dataCorteExato = LocalDate.now().plusDays(31);
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(150)
                        .dataValidade(dataCorteExato)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueTransferivel(lotes);

        assertThat(resultado).isEqualTo(150);
    }

    @Test
    void calcularEstoqueValidoDeveConsiderarLoteComValidadeHoje() {
        LocalDate hoje = LocalDate.now();
        List<InventarioPontoDispensacaoInsumosPorLoteDTO> lotes = List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .quantidade(150)
                        .dataValidade(hoje)
                        .build()
        );

        int resultado = calculadorEstoqueService.calcularEstoqueValido(lotes);

        assertThat(resultado).isZero();
    }
}


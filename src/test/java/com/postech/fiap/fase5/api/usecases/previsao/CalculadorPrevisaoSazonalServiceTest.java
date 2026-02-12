package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioPontoDispensacaoInsumosPorLoteDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculadorPrevisaoSazonalServiceTest {

    @Mock
    private CalculadorConsumoMedioService calculadorConsumoMedioService;

    @Mock
    private CalculadorEstoqueService calculadorEstoqueService;

    @Mock
    private AvaliadorStatusPrevisaoService avaliadorStatusPrevisaoService;

    @InjectMocks
    private CalculadorPrevisaoSazonalService calculadorPrevisaoSazonalService;

    private InsumoMensalDTO insumo;
    private List<HistoricoConsumoMesAnosAnterioresDTO> historicoSazonal;

    @BeforeEach
    void setUp() {
        insumo = InsumoMensalDTO.builder()
                .idInsumo(1L)
                .nomeInsumo("Insumo Teste")
                .quantidade(500)
                .lotes(criarLotesValidos())
                .build();
    }

    @Test
    void calcularPrevisaoDeveDefinirSemDadosSazonaisQuandoHistoricoNulo() {
        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, null);

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isZero();
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(999);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("SEM_DADOS_SAZONAIS");

        verifyNoInteractions(calculadorConsumoMedioService);
        verifyNoInteractions(calculadorEstoqueService);
        verifyNoInteractions(avaliadorStatusPrevisaoService);
    }

    @Test
    void calcularPrevisaoDeveDefinirSemDadosSazonaisQuandoHistoricoVazio() {
        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, Collections.emptyList());

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isZero();
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(999);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("SEM_DADOS_SAZONAIS");

        verifyNoInteractions(calculadorConsumoMedioService);
        verifyNoInteractions(calculadorEstoqueService);
        verifyNoInteractions(avaliadorStatusPrevisaoService);
    }

    @Test
    void calcularPrevisaoDeveDefinirSemDadosSazonaisQuandoHistoricoNaoContemInsumo() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(999L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(300)
                        .build()
        );

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isZero();
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(999);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("SEM_DADOS_SAZONAIS");

        verifyNoInteractions(calculadorConsumoMedioService);
        verifyNoInteractions(calculadorEstoqueService);
        verifyNoInteractions(avaliadorStatusPrevisaoService);
    }

    @Test
    void calcularPrevisaoDeveCalcularCorretamenteComHistoricoValido() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(300)
                        .build(),
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2024)
                        .totalConsumo(600)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(15.0);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(300);
        when(avaliadorStatusPrevisaoService.avaliar(20)).thenReturn("CRITICO");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isEqualTo(15.0);
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(20);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("CRITICO");

        verify(calculadorConsumoMedioService).calcularMediaDiaria(anyList());
        verify(calculadorEstoqueService).calcularEstoqueValido(insumo.getLotes());
        verify(avaliadorStatusPrevisaoService).avaliar(20);
    }

    @Test
    void calcularPrevisaoDeveFiltrarApenasHistoricoDoInsumo() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(300)
                        .build(),
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(2L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(500)
                        .build(),
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2024)
                        .totalConsumo(400)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(10.0);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(200);
        when(avaliadorStatusPrevisaoService.avaliar(20)).thenReturn("CRITICO");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        verify(calculadorConsumoMedioService).calcularMediaDiaria(argThat(lista ->
                lista.size() == 2 &&
                lista.stream().allMatch(h -> h.getIdInsumo().equals(1L))
        ));
    }

    @Test
    void calcularPrevisaoDeveDefinirSemConsumoPrevistoquandoMediaDiariaZero() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(0)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(0.0);

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isZero();
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(999);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("SEM_CONSUMO_PREVISTO");

        verify(calculadorConsumoMedioService).calcularMediaDiaria(anyList());
        verifyNoInteractions(calculadorEstoqueService);
        verifyNoInteractions(avaliadorStatusPrevisaoService);
    }

    @Test
    void calcularPrevisaoDeveDefinirSemConsumoPrevistoquandoMediaDiariaNegativa() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(-100)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(-5.0);

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isEqualTo(-5.0);
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(999);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("SEM_CONSUMO_PREVISTO");

        verify(calculadorConsumoMedioService).calcularMediaDiaria(anyList());
        verifyNoInteractions(calculadorEstoqueService);
        verifyNoInteractions(avaliadorStatusPrevisaoService);
    }

    @Test
    void calcularPrevisaoDeveCalcularDiasRestantesCorretamente() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(300)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(10.0);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(500);
        when(avaliadorStatusPrevisaoService.avaliar(50)).thenReturn("NORMAL");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isEqualTo(10.0);
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(50);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("NORMAL");
    }

    @Test
    void calcularPrevisaoDeveArredondarDiasRestantesParaBaixo() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(300)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(7.5);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(100);
        when(avaliadorStatusPrevisaoService.avaliar(13)).thenReturn("ALERTA");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(13);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("ALERTA");
    }

    @Test
    void calcularPrevisaoDeveDefinirStatusCriticoQuandoDiasRestantesBaixo() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(300)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(50.0);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(250);
        when(avaliadorStatusPrevisaoService.avaliar(5)).thenReturn("CRITICO");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(5);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("CRITICO");
    }

    @Test
    void calcularPrevisaoDeveConsiderarApenasEstoqueValido() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(600)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(20.0);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(100);
        when(avaliadorStatusPrevisaoService.avaliar(5)).thenReturn("CRITICO");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        verify(calculadorEstoqueService).calcularEstoqueValido(insumo.getLotes());
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(5);
    }

    @Test
    void calcularPrevisaoDeveCalcularComMultiplosAnosDeHistorico() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(2)
                        .ano(2025)
                        .totalConsumo(300)
                        .build(),
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(2)
                        .ano(2024)
                        .totalConsumo(600)
                        .build(),
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(2)
                        .ano(2023)
                        .totalConsumo(450)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(15.0);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(450);
        when(avaliadorStatusPrevisaoService.avaliar(30)).thenReturn("NORMAL");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getConsumoMedioDiarioSazonal()).isEqualTo(15.0);
        assertThat(insumo.getPrevisaoEsgotamentoDiasSazonal()).isEqualTo(30);
        assertThat(insumo.getStatusPrevisaoSazonal()).isEqualTo("NORMAL");

        verify(calculadorConsumoMedioService).calcularMediaDiaria(argThat(lista -> lista.size() == 3));
    }

    @Test
    void calcularPrevisaoDeveManterOutrosAtributosDoInsumo() {
        historicoSazonal = List.of(
                HistoricoConsumoMesAnosAnterioresDTO.builder()
                        .idInsumo(1L)
                        .mes(1)
                        .ano(2025)
                        .totalConsumo(300)
                        .build()
        );

        when(calculadorConsumoMedioService.calcularMediaDiaria(anyList())).thenReturn(10.0);
        when(calculadorEstoqueService.calcularEstoqueValido(anyList())).thenReturn(300);
        when(avaliadorStatusPrevisaoService.avaliar(30)).thenReturn("NORMAL");

        calculadorPrevisaoSazonalService.calcularPrevisao(insumo, historicoSazonal);

        assertThat(insumo.getIdInsumo()).isEqualTo(1L);
        assertThat(insumo.getNomeInsumo()).isEqualTo("Insumo Teste");
        assertThat(insumo.getQuantidade()).isEqualTo(500);
        assertThat(insumo.getLotes()).isNotEmpty();
    }

    private List<InventarioPontoDispensacaoInsumosPorLoteDTO> criarLotesValidos() {
        LocalDate dataFutura = LocalDate.now().plusDays(60);
        return List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .idLote(1L)
                        .idInsumo(1L)
                        .numeroLote("LOTE001")
                        .quantidade(300)
                        .dataValidade(dataFutura)
                        .nomeInsumo("Insumo Teste")
                        .build(),
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .idLote(2L)
                        .idInsumo(1L)
                        .numeroLote("LOTE002")
                        .quantidade(200)
                        .dataValidade(dataFutura)
                        .nomeInsumo("Insumo Teste")
                        .build()
        );
    }
}


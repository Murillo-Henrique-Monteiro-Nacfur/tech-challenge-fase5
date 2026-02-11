package com.postech.fiap.fase5.api.usecases;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoPorDiaDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.HistoricoConsumoRepository;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoMesAnosAnterioresProjection;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.usecases.estimativa.builder.InventarioDiarioBuilder;
import com.postech.fiap.fase5.api.usecases.estimativa.builder.InventarioMensalBuilder;
import com.postech.fiap.fase5.api.usecases.estimativa.converter.HistoricoConsumoDiarioConverter;
import com.postech.fiap.fase5.api.usecases.estimativa.converter.HistoricoConsumoMensalConverter;
import com.postech.fiap.fase5.api.usecases.estimativa.provider.DataProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DadosEstimativaUseCaseTest {

    @Mock
    private PontoDispensacaoRepository pontoDispensacaoRepository;

    @Mock
    private LoteInventarioRepository loteInventarioRepository;

    @Mock
    private HistoricoConsumoRepository historicoConsumoRepository;

    @Mock
    private InventarioDiarioBuilder inventarioDiarioBuilder;

    @Mock
    private InventarioMensalBuilder inventarioMensalBuilder;

    @Mock
    private HistoricoConsumoDiarioConverter historicoConsumoDiarioConverter;

    @Mock
    private HistoricoConsumoMensalConverter historicoConsumoMensalConverter;

    @Mock
    private DataProvider dataProvider;

    @InjectMocks
    private DadosEstimativaUseCase dadosEstimativaUseCase;

    @Test
    void deveExecutarEstimativaDiariaComSucesso() {
        PontoDispensacao ponto1 = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        PontoDispensacao ponto2 = criarPontoDispensacao(2L, "CNES002", "Ponto 2");
        List<PontoDispensacao> pontosDispensacao = List.of(ponto1, ponto2);

        LoteInventarioProjection lote1 = mock(LoteInventarioProjection.class);
        when(lote1.getIdPontoDispensacao()).thenReturn(1L);
        LoteInventarioProjection lote2 = mock(LoteInventarioProjection.class);
        when(lote2.getIdPontoDispensacao()).thenReturn(2L);
        List<LoteInventarioProjection> lotes = List.of(lote1, lote2);

        HistoricoConsumoPorDiaProjection historico1 = mock(HistoricoConsumoPorDiaProjection.class);
        List<HistoricoConsumoPorDiaProjection> historicoProjections = List.of(historico1);

        HistoricoConsumoPorDiaDTO historicoDTO1 = HistoricoConsumoPorDiaDTO.builder().build();
        List<HistoricoConsumoPorDiaDTO> historicoDTOs = List.of(historicoDTO1);

        InventarioDiarioDTO inventario1 = InventarioDiarioDTO.builder().build();
        InventarioDiarioDTO inventario2 = InventarioDiarioDTO.builder().build();

        LocalDateTime dataInicio = LocalDateTime.of(2026, 1, 11, 0, 0);
        LocalDateTime dataFim = LocalDateTime.of(2026, 2, 10, 23, 59);

        when(pontoDispensacaoRepository.findAll()).thenReturn(pontosDispensacao);
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(lotes);
        when(dataProvider.obterDataInicioHistoricoDiario()).thenReturn(dataInicio);
        when(dataProvider.obterDataFimHistoricoDiario()).thenReturn(dataFim);
        when(historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim))
                .thenReturn(historicoProjections);
        when(historicoConsumoDiarioConverter.toDTO(historico1)).thenReturn(historicoDTO1);
        when(inventarioDiarioBuilder.build(eq(ponto1), any(Map.class), eq(historicoDTOs)))
                .thenReturn(inventario1);
        when(inventarioDiarioBuilder.build(eq(ponto2), any(Map.class), eq(historicoDTOs)))
                .thenReturn(inventario2);

        List<InventarioDiarioDTO> resultado = dadosEstimativaUseCase.executeDiaria();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(inventario1, resultado.get(0));
        assertEquals(inventario2, resultado.get(1));

        verify(pontoDispensacaoRepository).findAll();
        verify(loteInventarioRepository).findAllLotePorInventario();
        verify(dataProvider).obterDataInicioHistoricoDiario();
        verify(dataProvider).obterDataFimHistoricoDiario();
        verify(historicoConsumoRepository).buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim);
        verify(historicoConsumoDiarioConverter).toDTO(historico1);
        verify(inventarioDiarioBuilder, times(2)).build(any(PontoDispensacao.class), any(Map.class), eq(historicoDTOs));
    }

    @Test
    void deveExecutarEstimativaDiariaComListaVazia() {
        List<PontoDispensacao> pontosDispensacaoVazia = List.of();
        List<LoteInventarioProjection> lotesVazio = List.of();
        List<HistoricoConsumoPorDiaProjection> historicoVazio = List.of();

        LocalDateTime dataInicio = LocalDateTime.of(2026, 1, 11, 0, 0);
        LocalDateTime dataFim = LocalDateTime.of(2026, 2, 10, 23, 59);

        when(pontoDispensacaoRepository.findAll()).thenReturn(pontosDispensacaoVazia);
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(lotesVazio);
        when(dataProvider.obterDataInicioHistoricoDiario()).thenReturn(dataInicio);
        when(dataProvider.obterDataFimHistoricoDiario()).thenReturn(dataFim);
        when(historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim))
                .thenReturn(historicoVazio);

        List<InventarioDiarioDTO> resultado = dadosEstimativaUseCase.executeDiaria();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());

        verify(pontoDispensacaoRepository).findAll();
        verify(loteInventarioRepository).findAllLotePorInventario();
        verify(inventarioDiarioBuilder, never()).build(any(), any(), any());
    }

    @Test
    void deveExecutarEstimativaMensalComSucesso() {
        PontoDispensacao ponto1 = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        PontoDispensacao ponto2 = criarPontoDispensacao(2L, "CNES002", "Ponto 2");
        List<PontoDispensacao> pontosDispensacao = List.of(ponto1, ponto2);

        LoteInventarioProjection lote1 = mock(LoteInventarioProjection.class);
        when(lote1.getIdPontoDispensacao()).thenReturn(1L);
        LoteInventarioProjection lote2 = mock(LoteInventarioProjection.class);
        when(lote2.getIdPontoDispensacao()).thenReturn(2L);
        List<LoteInventarioProjection> lotes = List.of(lote1, lote2);

        HistoricoConsumoMesAnosAnterioresProjection historico1 = mock(HistoricoConsumoMesAnosAnterioresProjection.class);
        List<HistoricoConsumoMesAnosAnterioresProjection> historicoProjections = List.of(historico1);

        HistoricoConsumoMesAnosAnterioresDTO historicoDTO1 = HistoricoConsumoMesAnosAnterioresDTO.builder().build();
        List<HistoricoConsumoMesAnosAnterioresDTO> historicoDTOs = List.of(historicoDTO1);

        InventarioMensalDTO inventario1 = InventarioMensalDTO.builder().build();
        InventarioMensalDTO inventario2 = InventarioMensalDTO.builder().build();

        LocalDateTime proximoMes = LocalDateTime.of(2026, 3, 1, 0, 0);

        when(pontoDispensacaoRepository.findAll()).thenReturn(pontosDispensacao);
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(lotes);
        when(dataProvider.obterProximoMes()).thenReturn(proximoMes);
        when(historicoConsumoRepository.buscaHistoricoParaOMesDosUltimosCincoAnos(3, 2026))
                .thenReturn(historicoProjections);
        when(historicoConsumoMensalConverter.toDTO(historico1)).thenReturn(historicoDTO1);
        when(inventarioMensalBuilder.build(eq(ponto1), any(Map.class), eq(historicoDTOs)))
                .thenReturn(inventario1);
        when(inventarioMensalBuilder.build(eq(ponto2), any(Map.class), eq(historicoDTOs)))
                .thenReturn(inventario2);

        List<InventarioMensalDTO> resultado = dadosEstimativaUseCase.executeMensal();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals(inventario1, resultado.get(0));
        assertEquals(inventario2, resultado.get(1));

        verify(pontoDispensacaoRepository).findAll();
        verify(loteInventarioRepository).findAllLotePorInventario();
        verify(dataProvider).obterProximoMes();
        verify(historicoConsumoRepository).buscaHistoricoParaOMesDosUltimosCincoAnos(3, 2026);
        verify(historicoConsumoMensalConverter).toDTO(historico1);
        verify(inventarioMensalBuilder, times(2)).build(any(PontoDispensacao.class), any(Map.class), eq(historicoDTOs));
    }

    @Test
    void deveExecutarEstimativaMensalComListaVazia() {
        List<PontoDispensacao> pontosDispensacaoVazia = List.of();
        List<LoteInventarioProjection> lotesVazio = List.of();
        List<HistoricoConsumoMesAnosAnterioresProjection> historicoVazio = List.of();

        LocalDateTime proximoMes = LocalDateTime.of(2026, 3, 1, 0, 0);

        when(pontoDispensacaoRepository.findAll()).thenReturn(pontosDispensacaoVazia);
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(lotesVazio);
        when(dataProvider.obterProximoMes()).thenReturn(proximoMes);
        when(historicoConsumoRepository.buscaHistoricoParaOMesDosUltimosCincoAnos(3, 2026))
                .thenReturn(historicoVazio);

        List<InventarioMensalDTO> resultado = dadosEstimativaUseCase.executeMensal();

        assertNotNull(resultado);
        assertEquals(0, resultado.size());

        verify(pontoDispensacaoRepository).findAll();
        verify(loteInventarioRepository).findAllLotePorInventario();
        verify(inventarioMensalBuilder, never()).build(any(), any(), any());
    }

    @Test
    void deveAgruparLotesPorPontoDispensacaoCorretamente() {
        PontoDispensacao ponto1 = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        List<PontoDispensacao> pontosDispensacao = List.of(ponto1);

        LoteInventarioProjection lote1 = mock(LoteInventarioProjection.class);
        when(lote1.getIdPontoDispensacao()).thenReturn(1L);
        LoteInventarioProjection lote2 = mock(LoteInventarioProjection.class);
        when(lote2.getIdPontoDispensacao()).thenReturn(1L);
        List<LoteInventarioProjection> lotes = List.of(lote1, lote2);

        LocalDateTime dataInicio = LocalDateTime.of(2026, 1, 11, 0, 0);
        LocalDateTime dataFim = LocalDateTime.of(2026, 2, 10, 23, 59);

        when(pontoDispensacaoRepository.findAll()).thenReturn(pontosDispensacao);
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(lotes);
        when(dataProvider.obterDataInicioHistoricoDiario()).thenReturn(dataInicio);
        when(dataProvider.obterDataFimHistoricoDiario()).thenReturn(dataFim);
        when(historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim))
                .thenReturn(List.of());

        dadosEstimativaUseCase.executeDiaria();

        verify(inventarioDiarioBuilder).build(eq(ponto1), argThat(map ->
                map.containsKey(1L) && map.get(1L).size() == 2
        ), any());
    }

    @Test
    void deveConverterHistoricoConsumoDiarioCorretamente() {
        PontoDispensacao ponto1 = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        List<PontoDispensacao> pontosDispensacao = List.of(ponto1);

        LoteInventarioProjection lote1 = mock(LoteInventarioProjection.class);
        when(lote1.getIdPontoDispensacao()).thenReturn(1L);
        List<LoteInventarioProjection> lotes = List.of(lote1);

        HistoricoConsumoPorDiaProjection historico1 = mock(HistoricoConsumoPorDiaProjection.class);
        HistoricoConsumoPorDiaProjection historico2 = mock(HistoricoConsumoPorDiaProjection.class);
        List<HistoricoConsumoPorDiaProjection> historicoProjections = List.of(historico1, historico2);

        HistoricoConsumoPorDiaDTO historicoDTO1 = HistoricoConsumoPorDiaDTO.builder().build();
        HistoricoConsumoPorDiaDTO historicoDTO2 = HistoricoConsumoPorDiaDTO.builder().build();

        LocalDateTime dataInicio = LocalDateTime.of(2026, 1, 11, 0, 0);
        LocalDateTime dataFim = LocalDateTime.of(2026, 2, 10, 23, 59);

        when(pontoDispensacaoRepository.findAll()).thenReturn(pontosDispensacao);
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(lotes);
        when(dataProvider.obterDataInicioHistoricoDiario()).thenReturn(dataInicio);
        when(dataProvider.obterDataFimHistoricoDiario()).thenReturn(dataFim);
        when(historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim))
                .thenReturn(historicoProjections);
        when(historicoConsumoDiarioConverter.toDTO(historico1)).thenReturn(historicoDTO1);
        when(historicoConsumoDiarioConverter.toDTO(historico2)).thenReturn(historicoDTO2);

        dadosEstimativaUseCase.executeDiaria();

        verify(historicoConsumoDiarioConverter).toDTO(historico1);
        verify(historicoConsumoDiarioConverter).toDTO(historico2);
    }

    @Test
    void deveConverterHistoricoConsumoMensalCorretamente() {
        PontoDispensacao ponto1 = criarPontoDispensacao(1L, "CNES001", "Ponto 1");
        List<PontoDispensacao> pontosDispensacao = List.of(ponto1);

        LoteInventarioProjection lote1 = mock(LoteInventarioProjection.class);
        when(lote1.getIdPontoDispensacao()).thenReturn(1L);
        List<LoteInventarioProjection> lotes = List.of(lote1);

        HistoricoConsumoMesAnosAnterioresProjection historico1 = mock(HistoricoConsumoMesAnosAnterioresProjection.class);
        HistoricoConsumoMesAnosAnterioresProjection historico2 = mock(HistoricoConsumoMesAnosAnterioresProjection.class);
        List<HistoricoConsumoMesAnosAnterioresProjection> historicoProjections = List.of(historico1, historico2);

        HistoricoConsumoMesAnosAnterioresDTO historicoDTO1 = HistoricoConsumoMesAnosAnterioresDTO.builder().build();
        HistoricoConsumoMesAnosAnterioresDTO historicoDTO2 = HistoricoConsumoMesAnosAnterioresDTO.builder().build();

        LocalDateTime proximoMes = LocalDateTime.of(2026, 3, 1, 0, 0);

        when(pontoDispensacaoRepository.findAll()).thenReturn(pontosDispensacao);
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(lotes);
        when(dataProvider.obterProximoMes()).thenReturn(proximoMes);
        when(historicoConsumoRepository.buscaHistoricoParaOMesDosUltimosCincoAnos(3, 2026))
                .thenReturn(historicoProjections);
        when(historicoConsumoMensalConverter.toDTO(historico1)).thenReturn(historicoDTO1);
        when(historicoConsumoMensalConverter.toDTO(historico2)).thenReturn(historicoDTO2);

        dadosEstimativaUseCase.executeMensal();

        verify(historicoConsumoMensalConverter).toDTO(historico1);
        verify(historicoConsumoMensalConverter).toDTO(historico2);
    }

    @Test
    void deveUtilizarDataCorretaParaBuscaHistoricoDiario() {
        LocalDateTime dataInicio = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime dataFim = LocalDateTime.of(2026, 1, 31, 23, 59);

        when(pontoDispensacaoRepository.findAll()).thenReturn(List.of());
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(List.of());
        when(dataProvider.obterDataInicioHistoricoDiario()).thenReturn(dataInicio);
        when(dataProvider.obterDataFimHistoricoDiario()).thenReturn(dataFim);
        when(historicoConsumoRepository.buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim))
                .thenReturn(List.of());

        dadosEstimativaUseCase.executeDiaria();

        verify(dataProvider).obterDataInicioHistoricoDiario();
        verify(dataProvider).obterDataFimHistoricoDiario();
        verify(historicoConsumoRepository).buscaHistoricoPorDiaNosUltimosTrintaDias(dataInicio, dataFim);
    }

    @Test
    void deveUtilizarProximoMesParaBuscaHistoricoMensal() {
        LocalDateTime proximoMes = LocalDateTime.of(2026, 4, 1, 0, 0);

        when(pontoDispensacaoRepository.findAll()).thenReturn(List.of());
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(List.of());
        when(dataProvider.obterProximoMes()).thenReturn(proximoMes);
        when(historicoConsumoRepository.buscaHistoricoParaOMesDosUltimosCincoAnos(4, 2026))
                .thenReturn(List.of());

        dadosEstimativaUseCase.executeMensal();

        verify(dataProvider).obterProximoMes();
        verify(historicoConsumoRepository).buscaHistoricoParaOMesDosUltimosCincoAnos(4, 2026);
    }

    private PontoDispensacao criarPontoDispensacao(Long id, String cnes, String nome) {
        PontoDispensacao ponto = new PontoDispensacao();
        ponto.setId(id);
        ponto.setCnes(cnes);
        ponto.setNome(nome);
        ponto.setTipo("TIPO_TESTE");
        ponto.setEmailResponsavel("teste@teste.com");
        ponto.setClientId(1L);
        return ponto;
    }
}


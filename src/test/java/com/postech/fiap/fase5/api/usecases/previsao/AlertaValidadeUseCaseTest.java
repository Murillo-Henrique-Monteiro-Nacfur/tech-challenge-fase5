package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.AlertaValidadeDTO;
import com.postech.fiap.fase5.api.dto.estimativa.ItemRiscoValidadeDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import com.postech.fiap.fase5.api.repositories.LoteInventarioRepository;
import com.postech.fiap.fase5.api.repositories.PontoDispensacaoRepository;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import com.postech.fiap.fase5.api.services.NotificacaoService;
import com.postech.fiap.fase5.api.usecases.previsao.domain.AlertaValidadeFactory;
import com.postech.fiap.fase5.api.usecases.previsao.domain.ConsumoMedioDiarioCalculator;
import com.postech.fiap.fase5.api.usecases.previsao.domain.LoteValidadeProcessor;
import com.postech.fiap.fase5.api.usecases.previsao.ports.HistoricoConsumoProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertaValidadeUseCaseTest {

    @Mock
    private PontoDispensacaoRepository pontoDispensacaoRepository;

    @Mock
    private LoteInventarioRepository loteInventarioRepository;

    @Mock
    private HistoricoConsumoProvider historicoConsumoProvider;

    @Mock
    private ConsumoMedioDiarioCalculator consumoCalculator;

    @Mock
    private LoteValidadeProcessor loteProcessor;

    @Mock
    private AlertaValidadeFactory alertaFactory;

    @Mock
    private NotificacaoService notificacaoService;

    @InjectMocks
    private AlertaValidadeUseCase alertaValidadeUseCase;

    private PontoDispensacao pontoDispensacao;
    private LoteInventarioProjection loteInventarioProjection;
    private HistoricoConsumoPorDiaProjection historicoConsumoPorDiaProjection;
    private ItemRiscoValidadeDTO itemRiscoValidadeDTO;
    private AlertaValidadeDTO alertaValidadeDTO;

    @BeforeEach
    void setUp() {
        pontoDispensacao = new PontoDispensacao();
        pontoDispensacao.setId(1L);
        pontoDispensacao.setCnes("1234567");
        pontoDispensacao.setNome("Farmacia Central");
        pontoDispensacao.setTipo("FARMACIA");
        pontoDispensacao.setEmailResponsavel("responsavel@email.com");

        loteInventarioProjection = criarLoteInventarioProjection();
        historicoConsumoPorDiaProjection = criarHistoricoConsumoPorDiaProjection();

        itemRiscoValidadeDTO = ItemRiscoValidadeDTO.builder()
                .idInsumo(100L)
                .nomeInsumo("Insulina NPH")
                .numeroLote("LOTE123")
                .dataValidade(LocalDate.now().plusDays(15))
                .quantidadeAtual(500)
                .consumoMedioDiario(10.0)
                .quantidadeDesperdicioPrevisto(350)
                .diasParaVencer(15)
                .build();

        alertaValidadeDTO = AlertaValidadeDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .itensEmRisco(List.of(itemRiscoValidadeDTO))
                .build();
    }

    @Test
    void executeDeveRetornarListaDeAlertasQuandoExistiremItensEmRisco() {
        when(pontoDispensacaoRepository.findAll()).thenReturn(List.of(pontoDispensacao));
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(List.of(loteInventarioProjection));
        when(historicoConsumoProvider.buscarHistoricoRecente(any())).thenReturn(List.of(historicoConsumoPorDiaProjection));
        when(consumoCalculator.calcularMediasPorPontoEInsumo(anyList())).thenReturn(Map.of(1L, Map.of(100L, 10.0)));
        when(loteProcessor.processarLotes(anyList(), anyMap(), any(LocalDate.class))).thenReturn(List.of(itemRiscoValidadeDTO));
        when(alertaFactory.criar(any(PontoDispensacao.class), anyList())).thenReturn(alertaValidadeDTO);

        List<AlertaValidadeDTO> resultado = alertaValidadeUseCase.execute();

        assertThat(resultado).isNotNull();
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPontoDispensacao()).isEqualTo(pontoDispensacao);
        assertThat(resultado.get(0).getItensEmRisco()).containsExactly(itemRiscoValidadeDTO);
        verify(notificacaoService).notificarRiscoValidade(resultado);
    }

    @Test
    void executeDeveRetornarListaVaziaQuandoNaoExistiremPontosDispensacao() {
        when(pontoDispensacaoRepository.findAll()).thenReturn(Collections.emptyList());
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(Collections.emptyList());
        when(historicoConsumoProvider.buscarHistoricoRecente(any())).thenReturn(Collections.emptyList());
        when(consumoCalculator.calcularMediasPorPontoEInsumo(anyList())).thenReturn(Collections.emptyMap());

        List<AlertaValidadeDTO> resultado = alertaValidadeUseCase.execute();

        assertThat(resultado).isEmpty();
        verify(notificacaoService).notificarRiscoValidade(resultado);
    }

    @Test
    void executeDeveRetornarListaVaziaQuandoNaoExistiremItensEmRisco() {
        when(pontoDispensacaoRepository.findAll()).thenReturn(List.of(pontoDispensacao));
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(List.of(loteInventarioProjection));
        when(historicoConsumoProvider.buscarHistoricoRecente(any())).thenReturn(List.of(historicoConsumoPorDiaProjection));
        when(consumoCalculator.calcularMediasPorPontoEInsumo(anyList())).thenReturn(Map.of(1L, Map.of(100L, 10.0)));
        when(loteProcessor.processarLotes(anyList(), anyMap(), any(LocalDate.class))).thenReturn(Collections.emptyList());

        List<AlertaValidadeDTO> resultado = alertaValidadeUseCase.execute();

        assertThat(resultado).isEmpty();
        verify(notificacaoService).notificarRiscoValidade(resultado);
        verify(alertaFactory, never()).criar(any(), anyList());
    }

    @Test
    void executeDeveProcessarMultiplosPontosDispensacaoCorretamente() {
        PontoDispensacao segundoPonto = new PontoDispensacao();
        segundoPonto.setId(2L);
        segundoPonto.setCnes("7654321");
        segundoPonto.setNome("Farmacia Auxiliar");
        segundoPonto.setTipo("FARMACIA");
        segundoPonto.setEmailResponsavel("auxiliar@email.com");

        LoteInventarioProjection loteSegundoPonto = criarLoteInventarioProjectionParaPonto(2L);

        ItemRiscoValidadeDTO itemSegundoPonto = ItemRiscoValidadeDTO.builder()
                .idInsumo(200L)
                .nomeInsumo("Amoxicilina")
                .numeroLote("LOTE456")
                .dataValidade(LocalDate.now().plusDays(10))
                .quantidadeAtual(200)
                .consumoMedioDiario(5.0)
                .quantidadeDesperdicioPrevisto(150)
                .diasParaVencer(10)
                .build();

        AlertaValidadeDTO alertaSegundoPonto = AlertaValidadeDTO.builder()
                .pontoDispensacao(segundoPonto)
                .itensEmRisco(List.of(itemSegundoPonto))
                .build();

        when(pontoDispensacaoRepository.findAll()).thenReturn(List.of(pontoDispensacao, segundoPonto));
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(List.of(loteInventarioProjection, loteSegundoPonto));
        when(historicoConsumoProvider.buscarHistoricoRecente(any())).thenReturn(List.of(historicoConsumoPorDiaProjection));
        when(consumoCalculator.calcularMediasPorPontoEInsumo(anyList())).thenReturn(
                Map.of(1L, Map.of(100L, 10.0), 2L, Map.of(200L, 5.0)));

        when(loteProcessor.processarLotes(anyList(), eq(Map.of(100L, 10.0)), any(LocalDate.class)))
                .thenReturn(List.of(itemRiscoValidadeDTO));
        when(loteProcessor.processarLotes(anyList(), eq(Map.of(200L, 5.0)), any(LocalDate.class)))
                .thenReturn(List.of(itemSegundoPonto));

        when(alertaFactory.criar(eq(pontoDispensacao), anyList())).thenReturn(alertaValidadeDTO);
        when(alertaFactory.criar(eq(segundoPonto), anyList())).thenReturn(alertaSegundoPonto);

        List<AlertaValidadeDTO> resultado = alertaValidadeUseCase.execute();

        assertThat(resultado).hasSize(2);
        verify(notificacaoService).notificarRiscoValidade(resultado);
    }

    @Test
    void executeDeveIgnorarPontosSemLotesAssociados() {
        PontoDispensacao pontoSemLote = new PontoDispensacao();
        pontoSemLote.setId(3L);
        pontoSemLote.setCnes("9999999");
        pontoSemLote.setNome("Farmacia Vazia");
        pontoSemLote.setTipo("FARMACIA");

        when(pontoDispensacaoRepository.findAll()).thenReturn(List.of(pontoDispensacao, pontoSemLote));
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(List.of(loteInventarioProjection));
        when(historicoConsumoProvider.buscarHistoricoRecente(any())).thenReturn(List.of(historicoConsumoPorDiaProjection));
        when(consumoCalculator.calcularMediasPorPontoEInsumo(anyList())).thenReturn(Map.of(1L, Map.of(100L, 10.0)));
        when(loteProcessor.processarLotes(eq(List.of(loteInventarioProjection)), anyMap(), any(LocalDate.class)))
                .thenReturn(List.of(itemRiscoValidadeDTO));
        when(loteProcessor.processarLotes(eq(Collections.emptyList()), anyMap(), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
        when(alertaFactory.criar(eq(pontoDispensacao), anyList())).thenReturn(alertaValidadeDTO);

        List<AlertaValidadeDTO> resultado = alertaValidadeUseCase.execute();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getPontoDispensacao()).isEqualTo(pontoDispensacao);
    }

    @Test
    void executeDeveChamarNotificacaoServiceComAlertasGerados() {
        when(pontoDispensacaoRepository.findAll()).thenReturn(List.of(pontoDispensacao));
        when(loteInventarioRepository.findAllLotePorInventario()).thenReturn(List.of(loteInventarioProjection));
        when(historicoConsumoProvider.buscarHistoricoRecente(any())).thenReturn(List.of(historicoConsumoPorDiaProjection));
        when(consumoCalculator.calcularMediasPorPontoEInsumo(anyList())).thenReturn(Map.of(1L, Map.of(100L, 10.0)));
        when(loteProcessor.processarLotes(anyList(), anyMap(), any(LocalDate.class))).thenReturn(List.of(itemRiscoValidadeDTO));
        when(alertaFactory.criar(any(PontoDispensacao.class), anyList())).thenReturn(alertaValidadeDTO);

        alertaValidadeUseCase.execute();

        verify(notificacaoService, times(1)).notificarRiscoValidade(anyList());
    }

    private LoteInventarioProjection criarLoteInventarioProjection() {
        return new LoteInventarioProjection() {
            @Override
            public Long getIdPontoDispensacao() {
                return 1L;
            }

            @Override
            public Long getIdInsumo() {
                return 100L;
            }

            @Override
            public String getNomeInsumo() {
                return "Insulina NPH";
            }

            @Override
            public Integer getQuantidade() {
                return 500;
            }

            @Override
            public Long getIdLote() {
                return 1L;
            }

            @Override
            public String getNumeroLote() {
                return "LOTE123";
            }

            @Override
            public LocalDate getDataValidade() {
                return LocalDate.now().plusDays(15);
            }
        };
    }

    private LoteInventarioProjection criarLoteInventarioProjectionParaPonto(Long idPonto) {
        return new LoteInventarioProjection() {
            @Override
            public Long getIdPontoDispensacao() {
                return idPonto;
            }

            @Override
            public Long getIdInsumo() {
                return 200L;
            }

            @Override
            public String getNomeInsumo() {
                return "Amoxicilina";
            }

            @Override
            public Integer getQuantidade() {
                return 200;
            }

            @Override
            public Long getIdLote() {
                return 2L;
            }

            @Override
            public String getNumeroLote() {
                return "LOTE456";
            }

            @Override
            public LocalDate getDataValidade() {
                return LocalDate.now().plusDays(10);
            }
        };
    }

    private HistoricoConsumoPorDiaProjection criarHistoricoConsumoPorDiaProjection() {
        return new HistoricoConsumoPorDiaProjection() {
            @Override
            public LocalDate getDia() {
                return LocalDate.now().minusDays(1);
            }

            @Override
            public Integer getTotalConsumo() {
                return 10;
            }

            @Override
            public Long getIdPontoDispensacao() {
                return 1L;
            }

            @Override
            public Long getIdInsumo() {
                return 100L;
            }
        };
    }
}


package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioPontoDispensacaoInsumosPorLoteDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeradorSugestoesTransferenciaServiceTest {

    @Mock
    private CalculadorEstoqueService calculadorEstoqueService;

    @Mock
    private AtribuidorSugestoesService atribuidorSugestoesService;

    @InjectMocks
    private GeradorSugestoesTransferenciaService geradorSugestoesTransferenciaService;

    private PontoDispensacao pontoDoador;
    private PontoDispensacao pontoReceptor;
    private InventarioMensalDTO inventarioDoador;
    private InventarioMensalDTO inventarioReceptor;

    @BeforeEach
    void setUp() {
        pontoDoador = new PontoDispensacao();
        pontoDoador.setId(1L);
        pontoDoador.setNome("Farmacia Doadora");
        pontoDoador.setCnes("1234567");
        pontoDoador.setTipo("FARMACIA");

        pontoReceptor = new PontoDispensacao();
        pontoReceptor.setId(2L);
        pontoReceptor.setNome("Farmacia Receptora");
        pontoReceptor.setCnes("7654321");
        pontoReceptor.setTipo("FARMACIA");
    }

    @Test
    void gerarSugestoesDeveProcessarInventariosVazios() {
        List<InventarioMensalDTO> inventarios = Collections.emptyList();

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verifyNoInteractions(calculadorEstoqueService);
        verifyNoInteractions(atribuidorSugestoesService);
    }

    @Test
    void gerarSugestoesDeveProcessarInventarioSemInsumos() {
        inventarioDoador = InventarioMensalDTO.builder()
                .pontoDispensacao(pontoDoador)
                .insumos(Collections.emptyList())
                .build();

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verifyNoInteractions(calculadorEstoqueService);
        verify(atribuidorSugestoesService, never()).atribuirSugestoes(any(), any(), any());
    }

    @Test
    void gerarSugestoesDeveIdentificarDoadorComExcedente() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 45, 20.0, 1000);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(1000);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(calculadorEstoqueService).calcularEstoqueTransferivel(insumo.getLotes());
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), any());
    }

    @Test
    void gerarSugestoesNaoDeveIdentificarDoadorComPrevisaoMenorQue30Dias() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 25, 20.0, 1000);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verifyNoInteractions(calculadorEstoqueService);
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), any());
    }

    @Test
    void gerarSugestoesNaoDeveIdentificarDoadorComPrevisaoExatos30Dias() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 30, 20.0, 1000);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verifyNoInteractions(calculadorEstoqueService);
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), any());
    }

    @Test
    void gerarSugestoesNaoDeveIdentificarDoadorComPrevisaoNula() {
        InsumoMensalDTO insumo = criarInsumoComPrevisaoNula(1L, "Insulina", 1000);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verifyNoInteractions(calculadorEstoqueService);
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), any());
    }

    @Test
    void gerarSugestoesNaoDeveIdentificarDoadorSemExcedente() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 45, 20.0, 500);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(500);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(calculadorEstoqueService).calcularEstoqueTransferivel(insumo.getLotes());
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), any());
    }

    @Test
    void gerarSugestoesDeveCalcularExcedenteCorretamente() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 60, 10.0, 800);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(800);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(calculadorEstoqueService).calcularEstoqueTransferivel(insumo.getLotes());
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), argThat(mapa -> {
            List<SugestaoTransferenciaDTO> sugestoes = mapa.get(1L);
            return sugestoes != null &&
                   sugestoes.size() == 1 &&
                   sugestoes.get(0).getQuantidadeDisponivelNoDoador() == 500;
        }));
    }

    @Test
    void gerarSugestoesDeveConsiderarConsumoMedioDiarioNulo() {
        InsumoMensalDTO insumo = criarInsumoComConsumoNulo(1L, "Insulina", 45, 800);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(800);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(calculadorEstoqueService).calcularEstoqueTransferivel(insumo.getLotes());
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), argThat(mapa -> {
            List<SugestaoTransferenciaDTO> sugestoes = mapa.get(1L);
            return sugestoes != null &&
                   sugestoes.size() == 1 &&
                   sugestoes.get(0).getQuantidadeDisponivelNoDoador() == 800;
        }));
    }

    @Test
    void gerarSugestoesDeveCriarSugestaoComDadosCorretos() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 45, 10.0, 800);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(800);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), argThat(mapa -> {
            List<SugestaoTransferenciaDTO> sugestoes = mapa.get(1L);
            if (sugestoes == null || sugestoes.isEmpty()) return false;
            SugestaoTransferenciaDTO sugestao = sugestoes.get(0);
            return sugestao.getIdPontoDoador().equals(1L) &&
                   sugestao.getNomePontoDoador().equals("Farmacia Doadora") &&
                   sugestao.getQuantidadeDisponivelNoDoador() == 500 &&
                   sugestao.getPrevisaoDiasDoador() == 45;
        }));
    }

    @Test
    void gerarSugestoesDeveProcessarMultiplosInsumos() {
        InsumoMensalDTO insumo1 = criarInsumoDoador(1L, "Insulina", 45, 10.0, 800);
        InsumoMensalDTO insumo2 = criarInsumoDoador(2L, "Paracetamol", 50, 5.0, 600);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo1, insumo2));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(insumo1.getLotes())).thenReturn(800);
        when(calculadorEstoqueService.calcularEstoqueTransferivel(insumo2.getLotes())).thenReturn(600);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(calculadorEstoqueService).calcularEstoqueTransferivel(insumo1.getLotes());
        verify(calculadorEstoqueService).calcularEstoqueTransferivel(insumo2.getLotes());
        verify(atribuidorSugestoesService, times(2)).atribuirSugestoes(eq(inventarioDoador), any(), any());
    }

    @Test
    void gerarSugestoesDeveProcessarMultiplosPontos() {
        InsumoMensalDTO insumoDoador = criarInsumoDoador(1L, "Insulina", 45, 10.0, 800);
        InsumoMensalDTO insumoReceptor = criarInsumoDoador(1L, "Insulina", 5, 10.0, 100);

        inventarioDoador = criarInventario(pontoDoador, List.of(insumoDoador));
        inventarioReceptor = criarInventario(pontoReceptor, List.of(insumoReceptor));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(insumoDoador.getLotes())).thenReturn(800);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador, inventarioReceptor);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumoDoador), any());
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioReceptor), eq(insumoReceptor), any());
    }

    @Test
    void gerarSugestoesDeveAgruparDoadoresPorInsumo() {
        InsumoMensalDTO insumoDoador1 = criarInsumoDoador(1L, "Insulina", 45, 10.0, 800);
        InsumoMensalDTO insumoDoador2 = criarInsumoDoador(1L, "Insulina", 50, 8.0, 700);

        inventarioDoador = criarInventario(pontoDoador, List.of(insumoDoador1));
        InventarioMensalDTO inventarioDoador2 = criarInventario(criarPonto(3L, "Farmacia 3"), List.of(insumoDoador2));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(insumoDoador1.getLotes())).thenReturn(800);
        when(calculadorEstoqueService.calcularEstoqueTransferivel(insumoDoador2.getLotes())).thenReturn(700);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador, inventarioDoador2);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumoDoador1), argThat(mapa -> {
            List<SugestaoTransferenciaDTO> sugestoes = mapa.get(1L);
            return sugestoes != null && sugestoes.size() == 2;
        }));
    }

    @Test
    void gerarSugestoesDeveCalcularExcedenteComArredondamento() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 45, 7.5, 500);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(500);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), argThat(mapa -> {
            List<SugestaoTransferenciaDTO> sugestoes = mapa.get(1L);
            return sugestoes != null &&
                   sugestoes.size() == 1 &&
                   sugestoes.get(0).getQuantidadeDisponivelNoDoador() == 275;
        }));
    }

    @Test
    void gerarSugestoesNaoDeveCriarSugestaoComExcedenteZero() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 45, 10.0, 300);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(300);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), argThat(mapa -> {
            List<SugestaoTransferenciaDTO> sugestoes = mapa.get(1L);
            return sugestoes == null || sugestoes.isEmpty();
        }));
    }

    @Test
    void gerarSugestoesNaoDeveCriarSugestaoComExcedenteNegativo() {
        InsumoMensalDTO insumo = criarInsumoDoador(1L, "Insulina", 45, 20.0, 400);
        inventarioDoador = criarInventario(pontoDoador, List.of(insumo));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(anyList())).thenReturn(400);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumo), argThat(mapa -> {
            List<SugestaoTransferenciaDTO> sugestoes = mapa.get(1L);
            return sugestoes == null || sugestoes.isEmpty();
        }));
    }

    @Test
    void gerarSugestoesDeveAtribuirSugestoesParaTodosInsumos() {
        InsumoMensalDTO insumoDoador = criarInsumoDoador(1L, "Insulina", 45, 10.0, 800);
        InsumoMensalDTO insumoReceptor = criarInsumoReceptor(2L, "Paracetamol", 10);

        inventarioDoador = criarInventario(pontoDoador, List.of(insumoDoador));
        inventarioReceptor = criarInventario(pontoReceptor, List.of(insumoReceptor));

        when(calculadorEstoqueService.calcularEstoqueTransferivel(insumoDoador.getLotes())).thenReturn(800);

        List<InventarioMensalDTO> inventarios = List.of(inventarioDoador, inventarioReceptor);

        geradorSugestoesTransferenciaService.gerarSugestoes(inventarios);

        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioDoador), eq(insumoDoador), any());
        verify(atribuidorSugestoesService).atribuirSugestoes(eq(inventarioReceptor), eq(insumoReceptor), any());
    }

    private InsumoMensalDTO criarInsumoDoador(Long idInsumo, String nome, Integer previsaoDias, Double consumoDiario, Integer estoqueTotal) {
        return InsumoMensalDTO.builder()
                .idInsumo(idInsumo)
                .nomeInsumo(nome)
                .quantidade(estoqueTotal)
                .previsaoEsgotamentoDiasSazonal(previsaoDias)
                .consumoMedioDiarioSazonal(consumoDiario)
                .lotes(criarLotes(idInsumo, estoqueTotal))
                .build();
    }

    private InsumoMensalDTO criarInsumoComPrevisaoNula(Long idInsumo, String nome, Integer estoqueTotal) {
        return InsumoMensalDTO.builder()
                .idInsumo(idInsumo)
                .nomeInsumo(nome)
                .quantidade(estoqueTotal)
                .previsaoEsgotamentoDiasSazonal(null)
                .consumoMedioDiarioSazonal(10.0)
                .lotes(criarLotes(idInsumo, estoqueTotal))
                .build();
    }

    private InsumoMensalDTO criarInsumoComConsumoNulo(Long idInsumo, String nome, Integer previsaoDias, Integer estoqueTotal) {
        return InsumoMensalDTO.builder()
                .idInsumo(idInsumo)
                .nomeInsumo(nome)
                .quantidade(estoqueTotal)
                .previsaoEsgotamentoDiasSazonal(previsaoDias)
                .consumoMedioDiarioSazonal(null)
                .lotes(criarLotes(idInsumo, estoqueTotal))
                .build();
    }

    private InsumoMensalDTO criarInsumoReceptor(Long idInsumo, String nome, Integer previsaoDias) {
        return InsumoMensalDTO.builder()
                .idInsumo(idInsumo)
                .nomeInsumo(nome)
                .quantidade(100)
                .previsaoEsgotamentoDiasSazonal(previsaoDias)
                .consumoMedioDiarioSazonal(5.0)
                .statusPrevisaoSazonal("CRITICO")
                .lotes(criarLotes(idInsumo, 100))
                .build();
    }

    private List<InventarioPontoDispensacaoInsumosPorLoteDTO> criarLotes(Long idInsumo, Integer quantidade) {
        return List.of(
                InventarioPontoDispensacaoInsumosPorLoteDTO.builder()
                        .idLote(1L)
                        .idInsumo(idInsumo)
                        .numeroLote("LOTE001")
                        .quantidade(quantidade)
                        .dataValidade(LocalDate.now().plusDays(90))
                        .build()
        );
    }

    private InventarioMensalDTO criarInventario(PontoDispensacao ponto, List<InsumoMensalDTO> insumos) {
        return InventarioMensalDTO.builder()
                .pontoDispensacao(ponto)
                .insumos(new ArrayList<>(insumos))
                .build();
    }

    private PontoDispensacao criarPonto(Long id, String nome) {
        PontoDispensacao ponto = new PontoDispensacao();
        ponto.setId(id);
        ponto.setNome(nome);
        ponto.setCnes("CNS" + id);
        ponto.setTipo("FARMACIA");
        return ponto;
    }
}


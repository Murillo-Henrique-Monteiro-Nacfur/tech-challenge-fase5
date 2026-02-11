package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AtribuidorSugestoesServiceTest {

    @InjectMocks
    private AtribuidorSugestoesService atribuidorSugestoesService;

    private PontoDispensacao pontoDispensacao;
    private InventarioMensalDTO inventarioMensalDTO;
    private InsumoMensalDTO insumoMensalDTO;
    private Map<Long, List<SugestaoTransferenciaDTO>> mapaDoadores;

    @BeforeEach
    void setUp() {
        pontoDispensacao = new PontoDispensacao();
        pontoDispensacao.setId(1L);
        pontoDispensacao.setCnes("1234567");
        pontoDispensacao.setNome("Farmacia Central");
        pontoDispensacao.setTipo("FARMACIA");

        inventarioMensalDTO = InventarioMensalDTO.builder()
                .pontoDispensacao(pontoDispensacao)
                .build();

        insumoMensalDTO = InsumoMensalDTO.builder()
                .idInsumo(100L)
                .nomeInsumo("Insulina NPH")
                .quantidade(500)
                .build();

        mapaDoadores = new HashMap<>();
    }

    @Test
    void atribuirSugestoesDeveAtribuirDoadoresQuandoStatusCritico() {
        insumoMensalDTO.setStatusPrevisaoSazonal("CRITICO");

        SugestaoTransferenciaDTO doadorValido = criarDoador(2L, "Farmacia Norte", 200, 30);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doadorValido)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).isNotNull();
        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).hasSize(1);
        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal().get(0).getIdPontoDoador()).isEqualTo(2L);
    }

    @Test
    void atribuirSugestoesDeveAtribuirDoadoresQuandoStatusAlerta() {
        insumoMensalDTO.setStatusPrevisaoSazonal("ALERTA");

        SugestaoTransferenciaDTO doadorValido = criarDoador(3L, "Farmacia Sul", 150, 25);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doadorValido)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).isNotNull();
        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).hasSize(1);
        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal().get(0).getIdPontoDoador()).isEqualTo(3L);
    }

    @Test
    void atribuirSugestoesNaoDeveAtribuirQuandoStatusNormal() {
        insumoMensalDTO.setStatusPrevisaoSazonal("NORMAL");

        SugestaoTransferenciaDTO doador = criarDoador(2L, "Farmacia Norte", 200, 30);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doador)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).isNull();
    }

    @Test
    void atribuirSugestoesNaoDeveAtribuirQuandoStatusNulo() {
        insumoMensalDTO.setStatusPrevisaoSazonal(null);

        SugestaoTransferenciaDTO doador = criarDoador(2L, "Farmacia Norte", 200, 30);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doador)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).isNull();
    }

    @Test
    void atribuirSugestoesDeveFiltrarDoadorMesmoPonto() {
        insumoMensalDTO.setStatusPrevisaoSazonal("CRITICO");

        SugestaoTransferenciaDTO doadorMesmoPonto = criarDoador(1L, "Farmacia Central", 100, 20);
        SugestaoTransferenciaDTO doadorOutroPonto = criarDoador(2L, "Farmacia Norte", 200, 30);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doadorMesmoPonto, doadorOutroPonto)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).hasSize(1);
        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal().get(0).getIdPontoDoador()).isEqualTo(2L);
    }

    @Test
    void atribuirSugestoesDeveRetornarListaVaziaQuandoTodosDoadoresSaoDoMesmoPonto() {
        insumoMensalDTO.setStatusPrevisaoSazonal("CRITICO");

        SugestaoTransferenciaDTO doadorMesmoPonto = criarDoador(1L, "Farmacia Central", 100, 20);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doadorMesmoPonto)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).isEmpty();
    }

    @Test
    void atribuirSugestoesDeveRetornarListaVaziaQuandoNaoExistemDoadoresParaInsumo() {
        insumoMensalDTO.setStatusPrevisaoSazonal("CRITICO");

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).isEmpty();
    }

    @Test
    void atribuirSugestoesDeveAtribuirMultiplosDoadoresValidos() {
        insumoMensalDTO.setStatusPrevisaoSazonal("ALERTA");

        SugestaoTransferenciaDTO doador1 = criarDoador(2L, "Farmacia Norte", 200, 30);
        SugestaoTransferenciaDTO doador2 = criarDoador(3L, "Farmacia Sul", 150, 25);
        SugestaoTransferenciaDTO doador3 = criarDoador(4L, "Farmacia Leste", 180, 28);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doador1, doador2, doador3)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).hasSize(3);
    }

    @Test
    void atribuirSugestoesDeveIgnorarDoadoresDeOutrosInsumos() {
        insumoMensalDTO.setStatusPrevisaoSazonal("CRITICO");

        SugestaoTransferenciaDTO doadorOutroInsumo = criarDoador(2L, "Farmacia Norte", 200, 30);
        mapaDoadores.put(999L, new ArrayList<>(List.of(doadorOutroInsumo)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).isEmpty();
    }

    @Test
    void atribuirSugestoesDeveManterDoadoresQuandoMapaContemMultiplosInsumos() {
        insumoMensalDTO.setStatusPrevisaoSazonal("CRITICO");

        SugestaoTransferenciaDTO doadorInsumo100 = criarDoador(2L, "Farmacia Norte", 200, 30);
        SugestaoTransferenciaDTO doadorOutroInsumo = criarDoador(3L, "Farmacia Sul", 150, 25);
        mapaDoadores.put(100L, new ArrayList<>(List.of(doadorInsumo100)));
        mapaDoadores.put(200L, new ArrayList<>(List.of(doadorOutroInsumo)));

        atribuidorSugestoesService.atribuirSugestoes(inventarioMensalDTO, insumoMensalDTO, mapaDoadores);

        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal()).hasSize(1);
        assertThat(insumoMensalDTO.getSugestoesTransferenciaSazonal().get(0).getIdPontoDoador()).isEqualTo(2L);
    }

    private SugestaoTransferenciaDTO criarDoador(Long idPonto, String nomePonto, Integer quantidade, Integer diasPrevisao) {
        return SugestaoTransferenciaDTO.builder()
                .idPontoDoador(idPonto)
                .nomePontoDoador(nomePonto)
                .quantidadeDisponivelNoDoador(quantidade)
                .previsaoDiasDoador(diasPrevisao)
                .build();
    }
}


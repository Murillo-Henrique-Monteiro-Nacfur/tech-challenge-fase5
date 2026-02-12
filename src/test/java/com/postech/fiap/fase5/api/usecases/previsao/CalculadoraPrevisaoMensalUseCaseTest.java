package com.postech.fiap.fase5.api.usecases.previsao;

import com.postech.fiap.fase5.api.dto.estimativa.HistoricoConsumoMesAnosAnterioresDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.InventarioMensalDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CalculadoraPrevisaoMensalUseCaseTest {

    @Mock
    private CalculadorPrevisaoSazonalService calculadorPrevisaoSazonalService;

    @Mock
    private GeradorSugestoesTransferenciaService geradorSugestoesTransferenciaService;

    @InjectMocks
    private CalculadoraPrevisaoMensalUseCase calculadoraPrevisaoMensalUseCase;

    @Test
    void deveProcessarPrevisoesESugestoesTransferencia() {
        List<HistoricoConsumoMesAnosAnterioresDTO> historico = new ArrayList<>();

        InsumoMensalDTO insumoUm = InsumoMensalDTO.builder().idInsumo(1L).build();
        InsumoMensalDTO insumoDois = InsumoMensalDTO.builder().idInsumo(2L).build();

        InventarioMensalDTO inventario = InventarioMensalDTO.builder()
                .historicoSazonal(historico)
                .insumos(List.of(insumoUm, insumoDois))
                .build();

        List<InventarioMensalDTO> inventarios = List.of(inventario);

        List<InventarioMensalDTO> resultado = calculadoraPrevisaoMensalUseCase.execute(inventarios);

        verify(calculadorPrevisaoSazonalService).calcularPrevisao(insumoUm, historico);
        verify(calculadorPrevisaoSazonalService).calcularPrevisao(insumoDois, historico);
        verify(geradorSugestoesTransferenciaService).gerarSugestoes(inventarios);

        assertEquals(inventarios, resultado);
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverInventarios() {
        List<InventarioMensalDTO> inventarios = List.of();

        List<InventarioMensalDTO> resultado = calculadoraPrevisaoMensalUseCase.execute(inventarios);

        assertEquals(0, resultado.size());
    }
}

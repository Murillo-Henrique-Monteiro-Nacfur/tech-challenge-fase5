package com.postech.fiap.fase5.api.usecases.previsao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class AvaliadorStatusPrevisaoServiceTest {

    private AvaliadorStatusPrevisaoService avaliadorStatusPrevisaoService;

    @BeforeEach
    void setUp() {
        avaliadorStatusPrevisaoService = new AvaliadorStatusPrevisaoService();
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 1, 5})
    void deveRetornarCriticoQuandoDiasRestantesAbaixoOuIgualAoLimiarCritico(int dias) {
        String resultado = avaliadorStatusPrevisaoService.avaliar(dias);
        assertThat(resultado).isEqualTo("CRITICO");
    }

    @ParameterizedTest
    @ValueSource(ints = {6, 10, 15})
    void deveRetornarAlertaQuandoDiasRestantesAcimaDoCriticoEAteOLimiarDeAlerta(int dias) {
        String resultado = avaliadorStatusPrevisaoService.avaliar(dias);
        assertThat(resultado).isEqualTo("ALERTA");
    }

    @ParameterizedTest
    @ValueSource(ints = {16, 20, 100})
    void deveRetornarNormalQuandoDiasRestantesAcimaDoLimiarDeAlerta(int dias) {
        String resultado = avaliadorStatusPrevisaoService.avaliar(dias);
        assertThat(resultado).isEqualTo("NORMAL");
    }
}


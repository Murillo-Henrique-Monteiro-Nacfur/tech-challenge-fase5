package com.postech.fiap.fase5.api.usecases.previsao.constantes;

public final class ConstantesPrevisao {

    private ConstantesPrevisao() {
    }

    public static final int PERIODO_ANALISE_DIAS = 30;
    public static final int LIMITE_STATUS_CRITICO_DIAS = 5;
    public static final int LIMITE_STATUS_ALERTA_DIAS = 15;
    public static final int LIMITE_VALIDADE_TRANSFERENCIA_DIAS = 30;
    public static final int LIMITE_ESTOQUE_DOACAO_DIAS = 30;
    public static final int VALOR_SEM_PREVISAO = 999;

    public static final String STATUS_CRITICO = "CRITICO";
    public static final String STATUS_ALERTA = "ALERTA";
    public static final String STATUS_NORMAL = "NORMAL";
    public static final String STATUS_SEM_CONSUMO = "SEM_CONSUMO_PREVISTO";
    public static final String STATUS_SEM_DADOS = "SEM_DADOS_HISTORICOS";
}


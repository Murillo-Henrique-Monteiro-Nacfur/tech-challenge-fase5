package com.postech.fiap.fase5.api.services.email.template;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoDiarioDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DiarioEmailTemplateBuilder extends AbstractEmailTemplateBuilder
        implements EmailTemplateBuilder<List<InsumoDiarioDTO>> {

    private static final String HEADER_GRADIENT = "#d32f2f 0%, #b71c1c 100%";
    private static final String HEADER_TITLE = "⚠️ Alerta de Desabastecimento";
    private static final String HEADER_SUBTITLE = "Sistema de Gestão de Insumos";
    private static final String HIGHLIGHT_COLOR = "#1976d2";

    private static final String DESCRIPTION =
            "Identificamos <strong>riscos de desabastecimento</strong> com base na análise do consumo dos últimos <strong>30 dias</strong>. " +
            "Por favor, revise os itens abaixo e tome as medidas necessárias.";

    private static final String CALL_TO_ACTION_BG = "#e3f2fd";
    private static final String CALL_TO_ACTION_COLOR = "#1565c0";
    private static final String CALL_TO_ACTION_MESSAGE =
            "<strong>📋 Ação Requerida:</strong> Por favor, tome as medidas necessárias para evitar o desabastecimento dos itens listados acima.";

    private static final String NO_TRANSFER_MESSAGE = "Nenhum ponto com excedente identificado para transferência.";

    @Override
    public String build(String nomePonto, List<InsumoDiarioDTO> items) {
        StringBuilder builder = new StringBuilder();

        builder.append(buildDocumentStart());
        builder.append(buildHeader(HEADER_GRADIENT, HEADER_TITLE, HEADER_SUBTITLE));
        builder.append(buildGreeting(nomePonto, HIGHLIGHT_COLOR));
        builder.append(buildDescription(DESCRIPTION));

        for (InsumoDiarioDTO item : items) {
            appendItemSection(builder, item);
        }

        builder.append(buildItemsTableEnd());
        builder.append(buildCallToAction(CALL_TO_ACTION_BG, CALL_TO_ACTION_COLOR, CALL_TO_ACTION_MESSAGE));
        builder.append(buildFooter());

        return builder.toString();
    }

    private void appendItemSection(StringBuilder builder, InsumoDiarioDTO item) {
        String statusColor = determineStatusColor(item.getStatusPrevisao());
        String statusBgColor = determineStatusBackgroundColor(item.getStatusPrevisao());
        String statusIcon = determineStatusIcon(item.getStatusPrevisao());

        builder.append(buildItemStart(statusColor));
        builder.append(buildItemHeader(item.getNomeInsumo(), statusBgColor, statusColor, statusIcon, item.getStatusPrevisao()));
        builder.append(buildItemHeaderClose());

        builder.append(buildDataRow(
                "Estoque Atual", String.valueOf(item.getQuantidade()), "unidades", "#333333",
                "Previsão de Término", String.valueOf(item.getPrevisaoEsgotamentoDias()), "dias", statusColor
        ));

        appendTransferSuggestions(builder, item.getSugestoesTransferencia());
        builder.append(buildItemEnd());
    }

    private void appendTransferSuggestions(StringBuilder builder, List<SugestaoTransferenciaDTO> sugestoes) {
        if (sugestoes != null && !sugestoes.isEmpty()) {
            builder.append(buildTransferSuggestionsHeader("Sugestões de Transferência"));
            for (SugestaoTransferenciaDTO sugestao : sugestoes) {
                builder.append(buildTransferSuggestionRow(sugestao.getNomePontoDoador(), sugestao.getQuantidadeDisponivelNoDoador()));
            }
            builder.append(buildTransferSuggestionsFooter());
        } else {
            builder.append(buildNoTransferSuggestions(NO_TRANSFER_MESSAGE));
        }
    }
}


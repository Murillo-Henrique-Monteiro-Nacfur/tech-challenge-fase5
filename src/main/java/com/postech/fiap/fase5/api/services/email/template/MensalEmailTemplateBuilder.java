package com.postech.fiap.fase5.api.services.email.template;

import com.postech.fiap.fase5.api.dto.estimativa.InsumoMensalDTO;
import com.postech.fiap.fase5.api.dto.estimativa.SugestaoTransferenciaDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MensalEmailTemplateBuilder extends AbstractEmailTemplateBuilder
        implements EmailTemplateBuilder<List<InsumoMensalDTO>> {

    private static final String HEADER_GRADIENT = "#7b1fa2 0%, #4a148c 100%";
    private static final String HEADER_TITLE = "📊 Previsão Sazonal de Estoque";
    private static final String HEADER_SUBTITLE = "Análise Mensal - Sistema de Gestão de Insumos";
    private static final String HIGHLIGHT_COLOR = "#7b1fa2";

    private static final String DESCRIPTION =
            "Identificamos <strong>riscos de desabastecimento para o próximo mês</strong> com base na análise do <strong>histórico de anos anteriores</strong>. " +
            "Esta previsão considera padrões sazonais de consumo. Recomendamos atenção especial aos itens listados.";

    private static final String CALL_TO_ACTION_BG = "#f3e5f5";
    private static final String CALL_TO_ACTION_COLOR = "#7b1fa2";
    private static final String CALL_TO_ACTION_MESSAGE =
            "<strong>📋 Planejamento Recomendado:</strong> Utilize esta previsão sazonal para antecipar necessidades de reposição e evitar o desabastecimento no próximo mês.";

    private static final String NO_TRANSFER_MESSAGE = "Nenhum ponto com excedente projetado identificado para transferência.";
    private static final String SEASONAL_BADGE = """
                                                    <span style="display: inline-block; margin-top: 8px; margin-left: 8px; padding: 4px 12px; background-color: #e8eaf6; color: #3949ab; border-radius: 20px; font-size: 12px;">
                                                        📅 Base Histórica
                                                    </span>
            """;

    @Override
    public String build(String nomePonto, List<InsumoMensalDTO> items) {
        StringBuilder builder = new StringBuilder();

        builder.append(buildDocumentStart());
        builder.append(buildHeader(HEADER_GRADIENT, HEADER_TITLE, HEADER_SUBTITLE));
        builder.append(buildGreeting(nomePonto, HIGHLIGHT_COLOR));
        builder.append(buildDescription(DESCRIPTION));

        for (InsumoMensalDTO item : items) {
            appendItemSection(builder, item);
        }

        builder.append(buildItemsTableEnd());
        builder.append(buildCallToAction(CALL_TO_ACTION_BG, CALL_TO_ACTION_COLOR, CALL_TO_ACTION_MESSAGE));
        builder.append(buildFooter());

        return builder.toString();
    }

    private void appendItemSection(StringBuilder builder, InsumoMensalDTO item) {
        String statusColor = determineStatusColor(item.getStatusPrevisaoSazonal());
        String statusBgColor = determineStatusBackgroundColor(item.getStatusPrevisaoSazonal());
        String statusIcon = determineStatusIcon(item.getStatusPrevisaoSazonal());

        builder.append(buildItemStart(statusColor));
        builder.append(buildItemHeader(item.getNomeInsumo(), statusBgColor, statusColor, statusIcon, item.getStatusPrevisaoSazonal()));
        builder.append(SEASONAL_BADGE);
        builder.append(buildItemHeaderClose());

        builder.append(buildDataRow(
                "Estoque Atual", String.valueOf(item.getQuantidade()), "unidades", "#333333",
                "Previsão de Término (Sazonal)", String.valueOf(item.getPrevisaoEsgotamentoDiasSazonal()), "dias", statusColor
        ));

        appendTransferSuggestions(builder, item.getSugestoesTransferenciaSazonal());
        builder.append(buildItemEnd());
    }

    private void appendTransferSuggestions(StringBuilder builder, List<SugestaoTransferenciaDTO> sugestoes) {
        if (sugestoes != null && !sugestoes.isEmpty()) {
            builder.append(buildTransferSuggestionsHeader("Sugestões de Transferência (Projeção)"));
            for (SugestaoTransferenciaDTO sugestao : sugestoes) {
                builder.append(buildTransferSuggestionRow(sugestao.getNomePontoDoador(), sugestao.getQuantidadeDisponivelNoDoador()));
            }
            builder.append(buildTransferSuggestionsFooter());
        } else {
            builder.append(buildNoTransferSuggestions(NO_TRANSFER_MESSAGE));
        }
    }
}


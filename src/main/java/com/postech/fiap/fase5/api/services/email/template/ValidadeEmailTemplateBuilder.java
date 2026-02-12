package com.postech.fiap.fase5.api.services.email.template;

import com.postech.fiap.fase5.api.dto.estimativa.ItemRiscoValidadeDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ValidadeEmailTemplateBuilder extends AbstractEmailTemplateBuilder
        implements EmailTemplateBuilder<List<ItemRiscoValidadeDTO>> {

    private static final String HEADER_GRADIENT = "#ff6f00 0%, #e65100 100%";
    private static final String HEADER_TITLE = "⏰ Alerta de Validade";
    private static final String HEADER_SUBTITLE = "Prevenção de Perdas - Sistema de Gestão de Insumos";
    private static final String HIGHLIGHT_COLOR = "#e65100";

    private static final String DESCRIPTION =
            "Identificamos <strong>lotes com risco de perda por validade</strong>. O consumo atual indica que haverá " +
            "<strong style=\"color: #d32f2f;\">desperdício</strong> se nenhuma ação for tomada. Por favor, revise os itens abaixo.";

    private static final String CALL_TO_ACTION_BG = "#fff3e0";
    private static final String CALL_TO_ACTION_COLOR = "#e65100";
    private static final String CALL_TO_ACTION_MESSAGE =
            "<strong>📋 Ação Recomendada:</strong> Priorize o uso destes lotes ou realize transferência imediata para outros pontos de dispensação com maior demanda.";

    private static final int URGENT_THRESHOLD_DAYS = 30;
    private static final String STATUS_URGENTE = "URGENTE";
    private static final String STATUS_ATENCAO = "ATENÇÃO";

    @Override
    public String build(String nomePonto, List<ItemRiscoValidadeDTO> items) {
        StringBuilder builder = new StringBuilder();

        builder.append(buildDocumentStart());
        builder.append(buildHeader(HEADER_GRADIENT, HEADER_TITLE, HEADER_SUBTITLE));
        builder.append(buildGreeting(nomePonto, HIGHLIGHT_COLOR));
        builder.append(buildDescription(DESCRIPTION));

        for (ItemRiscoValidadeDTO item : items) {
            appendItemSection(builder, item);
        }

        builder.append(buildItemsTableEnd());
        builder.append(buildCallToAction(CALL_TO_ACTION_BG, CALL_TO_ACTION_COLOR, CALL_TO_ACTION_MESSAGE));
        builder.append(buildFooter());

        return builder.toString();
    }

    private void appendItemSection(StringBuilder builder, ItemRiscoValidadeDTO item) {
        boolean isUrgente = item.getDiasParaVencer() <= URGENT_THRESHOLD_DAYS;
        String statusColor = isUrgente ? COLOR_CRITICO : COLOR_ALERTA;
        String statusBgColor = isUrgente ? BG_COLOR_CRITICO : BG_COLOR_ALERTA;
        String statusIcon = isUrgente ? ICON_CRITICO : ICON_ALERTA;
        String statusLabel = isUrgente ? STATUS_URGENTE : STATUS_ATENCAO;

        builder.append(buildItemStart(statusColor));
        builder.append(buildItemHeader(item.getNomeInsumo(), statusBgColor, statusColor, statusIcon, statusLabel));
        builder.append(buildLoteBadge(item.getNumeroLote()));
        builder.append(buildItemHeaderClose());

        builder.append(buildValidityDataRow(item, statusColor));
        builder.append(buildConsumoRow(item));
        builder.append(buildItemEnd());
    }

    private String buildLoteBadge(String numeroLote) {
        return String.format("""
                                                    <span style="display: inline-block; margin-top: 8px; margin-left: 8px; padding: 4px 12px; background-color: #e3f2fd; color: #1565c0; border-radius: 20px; font-size: 12px;">
                                                        📦 Lote: %s
                                                    </span>
            """, numeroLote);
    }

    private String buildValidityDataRow(ItemRiscoValidadeDTO item, String statusColor) {
        return String.format("""
                                            <tr>
                                                <td width="50%%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Data de Validade</p>
                                                    <p style="color: %s; font-size: 20px; font-weight: 600; margin: 0;">
                                                        %s
                                                    </p>
                                                    <p style="color: #757575; font-size: 12px; margin: 4px 0 0 0;">
                                                        Faltam <strong>%d</strong> dias
                                                    </p>
                                                </td>
                                                <td width="50%%" style="padding: 15px 0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Estoque Atual</p>
                                                    <p style="color: #333333; font-size: 20px; font-weight: 600; margin: 0;">
                                                        %d <span style="font-size: 14px; color: #757575;">unidades</span>
                                                    </p>
                                                </td>
                                            </tr>
            """, statusColor, item.getDataValidade(), item.getDiasParaVencer(), item.getQuantidadeAtual());
    }

    private String buildConsumoRow(ItemRiscoValidadeDTO item) {
        return String.format("""
                                            <tr>
                                                <td width="50%%" style="padding: 15px 0; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Consumo Médio</p>
                                                    <p style="color: #333333; font-size: 18px; font-weight: 600; margin: 0;">
                                                        %.2f <span style="font-size: 14px; color: #757575;">/dia</span>
                                                    </p>
                                                </td>
                                                <td width="50%%" style="padding: 15px 0; border-top: 1px solid #e0e0e0;">
                                                    <p style="color: #757575; font-size: 12px; margin: 0 0 4px 0; text-transform: uppercase;">Desperdício Previsto</p>
                                                    <p style="color: #d32f2f; font-size: 18px; font-weight: 600; margin: 0;">
                                                        ⚠️ %d <span style="font-size: 14px;">unidades</span>
                                                    </p>
                                                </td>
                                            </tr>
            """, item.getConsumoMedioDiario(), item.getQuantidadeDesperdicioPrevisto());
    }
}


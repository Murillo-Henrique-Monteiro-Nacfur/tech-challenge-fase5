package com.postech.fiap.fase5.api.repositories;

import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoMesAnosAnterioresProjection;
import com.postech.fiap.fase5.api.repositories.projections.HistoricoConsumoPorDiaProjection;
import com.postech.fiap.fase5.api.entities.HistoricoConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistoricoConsumoRepository extends JpaRepository<HistoricoConsumo, Long> {

    @Query("""
           SELECT
                DATE(hc.dataHora) AS dia,
                SUM(hc.quantidade) AS totalConsumo,
                hc.pontoDispensacao.id AS idPontoDispensacao,
                hc.lote.insumo.id AS idInsumo
            FROM
                HistoricoConsumo hc
            WHERE
                hc.dataHora >= :dataInicio AND hc.dataHora <= :dataFim
            GROUP BY
                DATE(hc.dataHora),
                hc.pontoDispensacao.id,
                hc.lote.insumo.id
            ORDER BY
                dia DESC
           """)
    List<HistoricoConsumoPorDiaProjection> buscaHistoricoPorDiaNosUltimosTrintaDias(
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );

    @Query("""
              SELECT
                 MONTH(hc.dataHora) AS mes,
                 YEAR(hc.dataHora) AS ano,
                 SUM(hc.quantidade) AS totalConsumo,
                 hc.pontoDispensacao.id AS idPontoDispensacao,
                 hc.lote.insumo.id AS idInsumo
                FROM
                 HistoricoConsumo hc
                WHERE
                 MONTH(hc.dataHora) = :mesAlvo
                 AND YEAR(hc.dataHora) < :anoAtual
                GROUP BY
                 YEAR(hc.dataHora),
                 MONTH(hc.dataHora),
                 hc.pontoDispensacao.id,
                 hc.lote.insumo.id
                ORDER BY
                 ano DESC
           """)
    List<HistoricoConsumoMesAnosAnterioresProjection> buscaHistoricoParaOProximoMesDosUltimosCincoAnos(
            @Param("mesAlvo") int mesAlvo,
            @Param("anoAtual") int anoAtual
    );
}

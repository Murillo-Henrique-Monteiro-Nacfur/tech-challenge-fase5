package com.postech.fiap.fase5.api.repositories;

import com.postech.fiap.fase5.api.entities.LoteInventario;
import com.postech.fiap.fase5.api.repositories.projections.LoteInventarioProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {
    Optional<LoteInventario> findByPontoDispensacaoIdAndLoteNumeroLote(Long pontoDispensacaoId, String numeroLote);
    Optional<LoteInventario> findByPontoDispensacaoIdAndLoteId(Long pontoDispensacaoId, Long loteId);

    @Query(value = """
            SELECT li.id_ponto_dispensacao as idPontoDispensacao,
                 i.id as idInsumo,
                 i.nome_generico as nomeInsumo,
                 sum(li.quantidade) as quantidade,
                 l.id as idLote,
                 l.numero_lote as numeroLote,
                 l.data_validade as dataValidade
           FROM public.lote_inventario li
               inner join public.lote l on (li.id_lote = l.id)
               inner join insumo i on (i.id = l.insumo_id)
               where l.data_validade > now()
               and li.quantidade > 0
               group by li.id_ponto_dispensacao, i.id,i.nome_generico,l.id,l.numero_lote, l.data_validade;
           """, nativeQuery = true)
    List<LoteInventarioProjection> findAllLotePorInventario();
}

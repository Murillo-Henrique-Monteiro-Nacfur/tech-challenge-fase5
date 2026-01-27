package com.postech.fiap.fase5.api.repositories;

import com.postech.fiap.fase5.api.entities.PontoDispensacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PontoDispensacaoRepository extends JpaRepository<PontoDispensacao, Long> {
    Optional<PontoDispensacao> findByIdAndClientId(Long id, Long clientId);

    @Query("""         
            SELECT pd
            FROM PontoDispensacao pd
            WHERE pd.clientId = :clientId
            AND pd.cnes = :cnes
        """)
    Optional<PontoDispensacao> findByClientIdAndCnes(@Param("clientId") Long clientId,@Param("cnes") String cnes);
}

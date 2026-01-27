package com.postech.fiap.fase5.api.repositories;

import com.postech.fiap.fase5.api.entities.LoteInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoteInventarioRepository extends JpaRepository<LoteInventario, Long> {
    Optional<LoteInventario> findByPontoDispensacaoIdAndLoteId(Long pontoDispensacaoId, Long loteId);
}

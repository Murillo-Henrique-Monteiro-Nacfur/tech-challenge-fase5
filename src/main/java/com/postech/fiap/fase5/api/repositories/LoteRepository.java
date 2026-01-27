package com.postech.fiap.fase5.api.repositories;

import com.postech.fiap.fase5.api.entities.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {
    Optional<Lote> findByNumeroLoteAndInsumoId(String numeroLote, Long insumoId);
}

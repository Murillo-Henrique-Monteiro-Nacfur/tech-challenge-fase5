package com.postech.fiap.fase5.api.repositories;

import com.postech.fiap.fase5.api.entities.HistoricoConsumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoConsumoRepository extends JpaRepository<HistoricoConsumo, Long> {
}

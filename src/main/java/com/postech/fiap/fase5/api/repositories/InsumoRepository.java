package com.postech.fiap.fase5.api.repositories;

import com.postech.fiap.fase5.api.entities.Insumo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    Optional<Insumo> findByCodigoCatmat(String codigoCatmat);
    boolean existsByCodigoCatmat(String codigoCatmat);
}

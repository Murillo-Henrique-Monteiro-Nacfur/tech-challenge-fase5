package com.postech.fiap.fase5.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "lote")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "numero_lote", nullable = false)
    private String numeroLote;

    @ManyToOne
    @JoinColumn(name = "insumo_id", nullable = false)
    private Insumo insumo;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Column(name = "data_fabricacao")
    private LocalDate dataFabricacao;

    @Column(name = "quantidade")
    private Integer quantidade;

}

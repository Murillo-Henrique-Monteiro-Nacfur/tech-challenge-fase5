package com.postech.fiap.fase5.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "insumo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator", sequenceName = "insumo_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo_catmat")
    private String codigoCatmat;

    @Column(name = "nome_generico", nullable = false)
    private String nomeGenerico;

    @Column(name = "forma_farmaceutica")
    private String formaFarmaceutica;

    private String marca;

    private String descricao;
}

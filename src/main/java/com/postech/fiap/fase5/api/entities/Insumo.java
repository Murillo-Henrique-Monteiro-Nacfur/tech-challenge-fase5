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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "codigo_catmat")
    private String codigoCatmat;

    @Column(name = "nome_generico", nullable = false)
    private String nomeGenerico;

    @Column(name = "forma_farmaceutica")
    private String formaFarmaceutica;

    @Column(name = "marca")
    private String marca;

    @Column(name = "descricao")
    private String descricao;
}

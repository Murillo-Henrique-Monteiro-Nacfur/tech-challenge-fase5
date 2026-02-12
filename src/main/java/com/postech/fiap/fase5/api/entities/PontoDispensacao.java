package com.postech.fiap.fase5.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ponto_dispensacao")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PontoDispensacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cnes", unique = true, nullable = false)
    private String cnes;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "email_responsavel")
    private String emailResponsavel;

    @Column(name = "client_id")
    private Long clientId;
}

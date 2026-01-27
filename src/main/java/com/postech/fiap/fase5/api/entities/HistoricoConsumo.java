package com.postech.fiap.fase5.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_consumo")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoricoConsumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ponto_dispensacao_id", nullable = false)
    private PontoDispensacao pontoDispensacao;

    @ManyToOne
    @JoinColumn(name = "id_lote_insumo", nullable = false)
    private Lote lote;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;

    @Column(name = "id_user")
    private Long idUser;
}

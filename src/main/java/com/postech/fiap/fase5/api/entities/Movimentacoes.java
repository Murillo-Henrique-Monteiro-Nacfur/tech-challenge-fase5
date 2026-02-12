package com.postech.fiap.fase5.api.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacoes")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Movimentacoes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ponto_dispensacao_origem_id", nullable = false)
    private PontoDispensacao pontoDispensacaoOrigem;

    @ManyToOne
    @JoinColumn(name = "ponto_dispensacao_destino_id", nullable = false)
    private PontoDispensacao pontoDispensacaoDestino;

    @ManyToOne
    @JoinColumn(name = "id_lote", nullable = false)
    private Lote lote;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "data_hora")
    private LocalDateTime dataHora;
}

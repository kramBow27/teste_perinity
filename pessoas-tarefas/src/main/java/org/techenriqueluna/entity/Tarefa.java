package org.techenriqueluna.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import javax.persistence.*;
import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Entity @Getter @Setter
public class Tarefa extends PanacheEntityBase {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    @Column(nullable = false)
    private LocalDate prazo;

    @Column(nullable = false)
    private String departamento;

    @Column(nullable = false)
    private Integer duracao; // horas estimadas/gastas

    @ManyToOne(fetch = FetchType.LAZY)
    private Pessoa pessoa;

    @Column(nullable = false)
    private boolean finalizado = false;
}
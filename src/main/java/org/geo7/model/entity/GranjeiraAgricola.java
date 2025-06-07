package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "granjeira_agricola", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GranjeiraAgricola implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "granjeira_agricola_seq")
    @SequenceGenerator(name = "granjeira_agricola_seq", sequenceName = "geo7.granjeira_agricola_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer codigo;

    @Column(nullable = false, length = 100)
    private String denominacao;

    @Column(nullable = false, length = 100)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "atividade_exploracao_agricola_id")
    private AtividadeExploracaoAgricola atividadeExploracaoAgricola;

    @Override
    public String toString() {
        return codigo + " - " + denominacao;
    }
}

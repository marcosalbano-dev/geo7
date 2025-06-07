package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "atividade_exploracao_agricola", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtividadeExploracaoAgricola implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "area_explorada")
    private Double areaExplorada;

    @Column(name = "indicador_restricao")
    private String indicadorRestricao;

    @OneToMany(mappedBy = "atividadeExploracaoAgricola", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<GranjeiraAgricola> granjeiraAgricola;
}

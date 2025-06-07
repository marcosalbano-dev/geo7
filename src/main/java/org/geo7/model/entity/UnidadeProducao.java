package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "unidade_producao", schema = "geo7")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnidadeProducao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unidade_producao_seq")
    @SequenceGenerator(name = "unidade_producao_seq", sequenceName = "geo7.unidade_producao_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false)
    private String unidade;

    @Column(name = "codigo_unidade", unique = true, nullable = false)
    private String codigoUnidade;

    @Override
    public String toString() {
        return codigoUnidade + " - " + unidade;
    }
}

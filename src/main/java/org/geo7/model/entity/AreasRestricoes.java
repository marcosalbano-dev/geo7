package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "areas_restricoes", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreasRestricoes implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "areas_restricoes_seq")
    @SequenceGenerator(name = "areas_restricoes_seq", sequenceName = "geo7.areas_restricoes_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(name = "tipo_area_restricao")
    private String tipoAreaRestricao;

    @Override
    public String toString() {
        return codigo + " - " + tipoAreaRestricao;
    }
}

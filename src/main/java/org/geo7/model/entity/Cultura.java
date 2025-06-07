package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.geo7.enums.NomeCategoria;
import org.geo7.enums.TipoCultura;

import java.io.Serializable;

@Entity
@Table(name = "cultura", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cultura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cultura_seq")
    @SequenceGenerator(name = "cultura_seq", sequenceName = "geo7.cultura_id_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cultura", nullable = false)
    private TipoCultura tipoCultura;

    @Column(name = "nome_cultura", nullable = false)
    private String nomeCultura;

    @Column(name = "codigo_cultura", unique = true, nullable = false)
    private Integer codigoCultura;

    @Override
    public String toString() {
        return nomeCultura + " - " + codigoCultura;
    }
}

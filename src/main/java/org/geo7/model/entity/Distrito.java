package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "distritos", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Distrito implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "distritos_seq")
    @SequenceGenerator(name = "distritos_seq", sequenceName = "distritos_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;

    @Column(nullable = false, length = 100)
    private String codigoDistrito;

    @Column(nullable = false, length = 100)
    private String nomeDistrito;

    @Override
    public String toString() {
        return nomeDistrito;
    }
}


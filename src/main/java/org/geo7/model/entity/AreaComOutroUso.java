package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "area_com_outro_uso", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AreaComOutroUso implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "area_com_outro_uso_seq")
    @SequenceGenerator(name = "area_com_outro_uso_seq", sequenceName = "geo7.area_com_outro_uso_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column
    private String denominacao;

    @Override
    public String toString() {
        return codigo + " - " + denominacao;
    }
}

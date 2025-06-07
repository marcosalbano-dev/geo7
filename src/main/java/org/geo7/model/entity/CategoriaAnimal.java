package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "categoria_animal", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaAnimal implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categoria_animal_seq")
    @SequenceGenerator(name = "categoria_animal_seq", sequenceName = "geo7.categoria_anima_id_seq", allocationSize = 1)
    private Long id;

    @Column(name = "denominao_categoria_animal", nullable = false)
    private String denominaoCategoriaAnimal;

    @Column(name = "codigo", unique = true, nullable = false)
    private String codigo;

    @Override
    public String toString() {
        return codigo + " - " + denominaoCategoriaAnimal;
    }
}

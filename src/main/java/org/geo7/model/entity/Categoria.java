package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.geo7.enums.NomeCategoria;

import java.io.Serializable;

@Entity
@Table(name = "categoria", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Categoria implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "categoria_seq")
    @SequenceGenerator(name = "categoria_seq", sequenceName = "geo7.categoria_id_seq", allocationSize = 1)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "nome_categoria", nullable = false, unique = true)
    private NomeCategoria nomeCategoria;

    @Override
    public String toString() {
        return nomeCategoria.toString();
    }
}

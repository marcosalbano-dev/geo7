package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "programa_governo", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramaGoverno implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "programa_governo_seq")
    @SequenceGenerator(
            name = "programa_governo_seq",
            sequenceName = "geo7.programa_governo_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    // Muitas ProgramaGoverno para Uma Pessoa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    // Uma ProgramaGoverno pode estar ligada a várias Pessoas
    @ManyToMany(mappedBy = "programasDoGoverno")
    private Set<Pessoa> pessoas = new HashSet<>();

    @Override
    public String toString() {
        return nome;
    }
}


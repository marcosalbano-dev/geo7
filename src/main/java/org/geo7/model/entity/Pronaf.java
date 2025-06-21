package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.util.Set;
@Entity
@Table(name = "pronaf", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pronaf implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pronaf_seq")
    @SequenceGenerator(
            name = "pronaf_seq",
            sequenceName = "geo7.pronaf_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(length = 50)
    private String tipo;
    @Column(length = 50)
    private String faixaI;
    @Column(length = 50)
    private String faixaII;
    @Column(length = 50)
    private String faixaIII;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", nullable = false)
    private Pessoa pessoa;

    @Override
    public String toString() {
        return tipo;
    }
}

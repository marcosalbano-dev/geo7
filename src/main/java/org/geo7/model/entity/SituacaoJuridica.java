package org.geo7.model.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "situacoes_juridicas", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SituacaoJuridica {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "situacoes_juridicas_seq")
    @SequenceGenerator(name = "situacoes_juridicas_seq", sequenceName = "situacoes_juridicas_id_seq", allocationSize = 1)
    private Long id;

    @Column(length = 50)
    private String nome;

    @OneToMany(mappedBy = "situacaoJuridica", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lote> lotes = new HashSet<>();

    @Override
    public String toString() {
        return nome;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SituacaoJuridica that = (SituacaoJuridica) obj;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

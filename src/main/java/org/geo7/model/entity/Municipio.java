package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "municipios", schema = "ibge")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Municipio {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "municipios_seq")
    @SequenceGenerator(name = "municipios_seq", sequenceName = "ibge.municipios_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;

    @Column(length = 2)
    private String uf;

    @Column(length = 100)
    private String regiao;

    @Column(length = 100)
    private String microregiao;

    @Column
    private Double latitude;
    @Column
    private Double longitude;
    @Column
    private Integer areaModuloFiscal;

    @Column(length = 100)
    private String mesoregiao;

    @OneToMany(mappedBy = "municipio", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Lote> lotes = new HashSet<>();

    // Conversão do método setNome para transformar em maiúsculo automaticamente
    public void setNome(String nome) {
        this.nome = nome != null ? nome.toUpperCase() : null;
    }

    @Override
    public String toString() {
        return nome + " - [ " + id + " ]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Municipio municipio = (Municipio) obj;
        return id != null && id.equals(municipio.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

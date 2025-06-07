package org.geo7.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lotes", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lotes_seq")
    @SequenceGenerator(name = "lotes_seq", sequenceName = "lotes_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 150)
    @NotEmpty(message = "{campo.proprietario.obrigatorio}")
    private String proprietario;

    @Column(precision = 18, scale = 4)
    private BigDecimal area;

    @Column
    private String denominacaoImovel;

    @Column(nullable = false, length = 50)
    private String numero;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dhc = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dhm = new Date();

    @Column
    private Double perimetro;

    @Column
    private String sncr;

    @Column(nullable = false, length = 11)
    @NotNull(message = "{campo.cpf.obrigatorio}")
    @CPF(message = "{campo.cpf.invalido}")
    private String cpf;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipio_id", nullable = false, referencedColumnName = "id")
    private Municipio municipio;

    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<FormaObtencao> formaObtencao = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_juridica_id")
    private SituacaoJuridica situacaoJuridica;

    @Column
    private String dataTerminoPeriodoDeUso;

    @Override
    public String toString() {
        return "Lote{" +
                "id=" + id +
                ", proprietario='" + proprietario + '\'' +
                ", numero='" + numero + '\'' +
                ", area=" + area +
                ", perimetro=" + perimetro +
                ", cpf='" + cpf + '\'' +
                ", municipio=" + (municipio != null ? municipio.getNome() : "N/A") +
                '}';
    }

}

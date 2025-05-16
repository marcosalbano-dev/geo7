package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "lotes", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "lotes_seq")
    @SequenceGenerator(name = "lotes_seq", sequenceName = "lotes_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 150)
    private String proprietario;

    @Column(precision = 18, scale = 4)
    private BigDecimal area;

    @Column
    private String denominacaoImovel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_juridica_id")
    private SituacaoJuridica situacaoJuridica;

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
    private String cpf;


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "municipio_id", nullable = false)
    private Municipio municipio;


//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "criado_por_id")
//    private SecUser criadoPor;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "modificado_por_id")
//    private SecUser modificadoPor;
//
//    @OneToMany(mappedBy = "lote", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<FormaObtencao> formaObtencao = new HashSet<>();


    @Override
    public String toString() {
        return numero;
    }

}

package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "endereco_lote", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnderecoLote implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "endereco_lote_seq")
    @SequenceGenerator(name = "endereco_lote_seq", sequenceName = "endereco_lote_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(nullable = false)
    private Boolean ativo = true;

    @PrePersist
    protected void onCreate() {
        dhc = new Date();
        dhm = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        dhm = new Date();
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dhc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dhm;

    @Column(length = 255)
    private String pontoDeReferencia;

    @Column(length = 100)
    private String codImoReceita;

    @Column(precision = 18, scale = 4)
    private BigDecimal areaUrbana = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "distrito_id")
    private Distrito distrito;

    @Column(length = 100)
    private String comunidade;

    @Column(length = 100)
    private String localidade;

    @Override
    public String toString() {
        return "Endereço do imóvel: lote_id=" + (lote != null ? lote.getId() : "null");
    }
}


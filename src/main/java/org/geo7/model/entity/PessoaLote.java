package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "pessoa_lote", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PessoaLote {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pessoa_lote_seq")
    @SequenceGenerator(name = "pessoa_lote_seq", sequenceName = "geo7.pessoa_lote_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated;

    @Column(length = 50)
    private String codigoImovelRural;

    @Column(length = 60)
    private String condicaoPessoaImovelRural;

    @Column(precision = 18, scale = 4)
    private BigDecimal percentDetencao = BigDecimal.ZERO;

    private Boolean isDeclarante = false;

    private Boolean isResideNoImovel;

    @Column(length = 2)
    private String tipoDoAto;

    private Double numeroAto;

    @Temporal(TemporalType.DATE)
    private Date dataAto;

    @Column(precision = 18, scale = 4)
    private BigDecimal quantidadeAreaCedida = BigDecimal.ZERO;

    @Column(length = 30)
    private String atividadePrincipalExploracao;

    @Column(length = 2)
    private String contrato; // "E" (Escrito) ou "V" (Verbal)

    @Temporal(TemporalType.DATE)
    private Date dataTerminoContrato;

    @Column(nullable = false)
    private Boolean isContratoPrazoIndeterminado = false;

    @PrePersist
    protected void onCreate() {
        this.dateCreated = new Date();
        this.lastUpdated = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdated = new Date();
    }

    @Override
    public String toString() {
        return lote != null ? lote.toString() : "Sem lote";
    }
}

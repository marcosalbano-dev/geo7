package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name = "estrutura", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estrutura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estrutura_seq")
    @SequenceGenerator(name = "estrutura_seq", sequenceName = "estrutura_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "criado_por_id", nullable = true)
//    private SecUser criadoPor;
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "modificado_por_id", nullable = true)
//    private SecUser modificadoPor;

    private boolean ativo = true;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dhc = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dhm = new Date();
    @Column
    private Integer familiasResidentes;
    @Column
    private Integer pessoasResidentes;
    @Column
    private Integer trabalhadoresComCarteira;
    @Column
    private Integer trabalhadoresSemCarteira;
    @Column
    private Integer maoDeObraFamiliar;

    @Column(precision = 18, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal valorDasBenfeitorias = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal valorOutrasAtividades = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal valorTerraNua = BigDecimal.ZERO;

    @Column(precision = 18, scale = 2)
    private BigDecimal areaIrrigada = BigDecimal.ZERO;
    @Column
    private String litigio;
    @Column
    private Boolean entregouMemorialPlanilha = false;
    @Column
    private String destinacaoDoImovel;
    @Column
    private String pontoDeReferencia;
    @Column
    private Integer numeroHerdeiros;
    @Column
    private Double porcentagemDetencao = 0.0;
    @Column
    private String obsLitigio;
    @Column
    private Boolean isFonteAguaExterna = false;
    @Column
    private Boolean isPossuiElergiaEletrica = false;
    @Column
    private Boolean isPossuiEnergiaAlternativa = false;
    @Column
    private String tipoEnergiaEletrica;
    @Column
    private Boolean isIrrigacao = false;
    @Column
    private Boolean isAcude = false;
    @Column
    private Boolean isAcudePerene = false;
    @Column
    private String usoDaguaAcude;
    @Column
    private Boolean isLagoa = false;
    @Column
    private Boolean isLagoaPerene = false;
    @Column
    private String usoDaguaLagoa;

    @Column
    private Boolean isPoco = false;
    @Column
    private Boolean isPocoPerene = false;
    @Column
    private String usoDaguaPoco;

    @Column
    private Boolean isRioOuRiacho = false;
    @Column
    private Boolean isRioOuRiachoPerene = false;
    @Column
    private String usoDaguaRioOuRiacho;
    @Column
    private Boolean isOlhoDagua = false;
    @Column
    private Boolean isOlhoDaguaPerene = false;
    @Column
    private String usoDaguaOlhoDagua;
    @Column
    private Boolean isRedeDeAbastecimento = false;

    @Override
    public String toString() {
        return "Estrutura do imóvel: " + (lote != null ? lote.toString() : "Sem lote");
    }

}


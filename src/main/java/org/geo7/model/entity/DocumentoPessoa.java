package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "documentos_pessoa", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoPessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "documentos_pessoa_seq")
    @SequenceGenerator(name = "documentos_pessoa_seq", sequenceName = "geo7.documentos_pessoa_id_seq", allocationSize = 1)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id", unique = true)
    private Pessoa pessoa;

    @Column(length = 50)
    private String tipoDocumentoIdentificacao;

    @Column(length = 50)
    private String numeroDocumentoIdentificacao;

    @Column(length = 50)
    private String orgaoEmissor;

    @Column(length = 2)
    private String ufOrgaoEmissor = "CE";

    @Column(length = 50)
    private String tipoNacionalidade;

    @Column(length = 14, unique = true)
    private String cpf;

    @Column(length = 6)
    private String codigoPaisOrigem = "931";

    @Column(length = 50)
    private String estadoCivil;

    @Column(length = 10)
    private String tipoPessoa; // "FISICA" ou "JURIDICA"

    @Column(length = 18, unique = true)
    private String cnpj;

    @Column(length = 50)
    private String naturezaJuridica;

    private Double capitalNacional;
    private Double capitalEstrangeiro;

    @Column(length = 50)
    private String registroJuntaComercial;

    @Column(length = 50)
    private String nomeFantasia;

    @Column(length = 6)
    private String codigoPaisSede;

    @Column(length = 6)
    private String ufPaisSede;

    @Column(length = 50)
    private String tipoDocumentoRepresentanteLegal;

    @Column(length = 50)
    private String numeroDocumentoRepresentanteLegal;

    @Column(length = 6)
    private String codigoPaisResidencia = "931";

    @Column(length = 50)
    private String tipoDePoder;

    @Column(length = 50)
    private String tipoDeGoverno;

    @Column(length = 6)
    private String percentCapitalNacional;

    @Column(length = 6)
    private String percentCapitalEstrangeiro;

    @Column(length = 50)
    private String pcePais;

    @Column(length = 50)
    private String pcePercentCapital;

    @Column(length = 255)
    private String obsevacoesQuadro7;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "naturalidade_id")
    private Municipio naturalidade;

    @Transient
    private String uf;

    public String getUf() {
        return naturalidade != null ? naturalidade.getUf() : "";
    }

    @Override
    public String toString() {
        return tipoDocumentoIdentificacao;
    }
}


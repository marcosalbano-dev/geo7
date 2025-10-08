package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "conjuge_pessoa", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConjugePessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "conjuge_pessoa_seq")
    @SequenceGenerator(
            name = "conjuge_pessoa_seq",
            sequenceName = "geo7.conjuge_pessoa_id_seq",
            allocationSize = 1
    )
    private Long id;

    @Column(nullable = false, length = 120)
    private String nome;

    /** Relacionamentos **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    @Column(length = 20)
    private String telefone;

    @Column(length = 100)
    private String email;

    @Column(length = 120)
    private String nomePai;

    @Column(length = 120)
    private String nomeMae;

    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(length = 12)
    private String sexoPessoa;

    @Column(length = 160)
    private String logradouro;

    @Column(length = 20)
    private String numero;

    @Column(length = 80)
    private String complemento;

    @Column(length = 100)
    private String bairro;

    /** uf/ufNaturalidade são derivados dos Municípios, ficam @Transient */
    @Transient
    private String uf;

    @Transient
    private String ufNaturalidade;

    @Column(length = 9)
    private String cep;

    @Column(length = 4)
    @Builder.Default
    private String codigoPaisResidencia = "931";

    @Column(length = 4)
    @Builder.Default
    private String codigoPaisOrigem = "931";

    // Documentação para Cônjuge
    @Column(length = 60)
    private String tipoDocumentoIdentificacao;

    @Column(length = 120)
    private String descricaoOutroDocumentoIdentificacao;

    @Column(length = 40)
    private String numeroDocumentoIdentificacao;

    @Column(length = 30)
    private String orgaoEmissor;

    @Column(length = 2)
    @Builder.Default
    private String ufOrgaoEmissor = "CE";

    @Column(length = 20)
    private String tipoNacionalidade; // "BRASILEIRA" | "ESTRANGEIRA" (pode virar enum)

    @Column(length = 14, unique = true)
    private String cpf;

    @Column(length = 20)
    private String racaCor; // pode virar enum

    @Column(length = 2)
    private String ufNaturalidadeSigla; // opcional, se quiser manter um cache da sigla

    @Builder.Default
    private Boolean conjugeOk = false;

    @Temporal(TemporalType.DATE)
    private Date validadeRne;

    /** Municípios (belongsTo em Grails) **/
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipio municipioResidencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "naturalidade_id")
    private Municipio municipioNaturalidade;

    /** ---------- Derivados / Helpers ---------- **/

    public String getUf() {
        if (municipioResidencia != null) {
            return municipioResidencia.getUf();
        }
        return null;
    }

    public String getUfNaturalidade() {
        if (municipioNaturalidade != null) {
            return municipioNaturalidade.getUf();
        }
        return null;
    }

    /** Uppercase padronizado como no exemplo de Pessoa */
    @PrePersist
    @PreUpdate
    private void normalizarCampos() {
        if (nome != null) nome = nome.toUpperCase();
        if (nomePai != null) nomePai = nomePai.toUpperCase();
        if (nomeMae != null) nomeMae = nomeMae.toUpperCase();
        if (orgaoEmissor != null) orgaoEmissor = orgaoEmissor.toUpperCase();
        if (bairro != null) bairro = bairro.toUpperCase();
        if (logradouro != null) logradouro = logradouro.toUpperCase();
        if (complemento != null) complemento = complemento.toUpperCase();
    }

    /** equals/hashCode por ID, como no seu padrão */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConjugePessoa)) return false;
        ConjugePessoa that = (ConjugePessoa) o;
        return id != null && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /** toString enxuto */
    @Override
    public String toString() {
        return nome;
    }
}

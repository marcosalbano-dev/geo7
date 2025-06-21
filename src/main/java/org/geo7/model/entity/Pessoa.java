package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "pessoa", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pessoa_seq")
    @SequenceGenerator(name = "pessoa_seq", sequenceName = "geo7.pessoa_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(length = 20)
    private String telefone;

    @Column(length = 20)
    private String fax;

    @Column(length = 10)
    private String ramal;

    @Column(length = 100)
    private String email;

    @Column(length = 100)
    private String nomePai;

    @Column(length = 100)
    private String nomeMae;

    @Temporal(TemporalType.DATE)
    private Date dataNascimento;

    @Column(length = 12)
    private String sexoPessoa; // Enum depois, se desejar

    private Boolean isEspolio = false;

    // Informações do Anexo
    @Column(length = 20)
    private String codigoPessoaIncra;

    @Column(length = 30)
    private String coordenadaEste;

    @Column(length = 30)
    private String coordenadaNorte;

    @Column(length = 60)
    private String atividadePrincipal;

    // Cônjuge
    @Column(length = 30)
    private String regimeDeBens;

    @Column(length = 20)
    private String dataCasamento; // pode ser Date, se preferir

    private Boolean isRecebePronaf = false;
    private Boolean isRecebeAjudoProgramaGoverno = false;
    private Integer qtdPronaf = 0;

    @Column(precision = 18, scale = 4)
    private BigDecimal valorTotalPronafs = BigDecimal.ZERO;

    @Column(length = 20)
    private String racaCor;

    // Relacionamentos (adicione as entidades conforme necessidade)
     @OneToMany(mappedBy = "pessoa", fetch = FetchType.LAZY)
     private Set<PessoaLote> pessoasLote;

    @OneToMany(mappedBy = "pessoa", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Pronaf> pronafs = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "pessoa_programa_governo",
            schema = "geo7",
            joinColumns = @JoinColumn(name = "pessoa_id"),
            inverseJoinColumns = @JoinColumn(name = "programa_governo_id")
    )
    private Set<ProgramaGoverno> programasDoGoverno = new HashSet<>();


     @OneToOne(mappedBy = "pessoa", cascade = CascadeType.ALL)
     private DocumentoPessoa documento;

    @PrePersist
    @PreUpdate
    private void formatarNomes() {
        if (nome != null) nome = nome.toUpperCase();
        if (nomePai != null) nomePai = nomePai.toUpperCase();
        if (nomeMae != null) nomeMae = nomeMae.toUpperCase();
    }

    @Override
    public String toString() {
        return nome;
    }
}


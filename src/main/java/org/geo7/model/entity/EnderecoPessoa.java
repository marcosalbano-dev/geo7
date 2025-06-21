package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "endereco_pessoa", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnderecoPessoa implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "endereco_pessoa_seq")
    @SequenceGenerator(name = "endereco_pessoa_seq", sequenceName = "geo7.endereco_pessoa_id_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, length = 100)
    private String logradouro;

    @Column(length = 100)
    private String complemento;

    @Column(length = 10)
    private String numero;

    @Column(length = 100)
    private String bairro;

    @Column(length = 10)
    private String cep;

    @Column(length = 10)
    private String codigoPaisResidencia = "931";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipio_id")
    private Municipio municipio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pessoa_id")
    private Pessoa pessoa;

    // Não persistente/transient (não será mapeado para o banco)
    @Transient
    private String uf;

    // Getter customizado para 'uf' baseado no município
    public String getUf() {
        return municipio != null ? municipio.getUf() : "";
    }

    @Override
    public String toString() {
        return logradouro;
    }
}


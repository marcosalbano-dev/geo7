package org.geo7.model.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "forma_obtencao", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FormaObtencao implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forma_obtencao_seq")
    @SequenceGenerator(name = "forma_obtencao_seq", sequenceName = "forma_obtecao_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "situacao_juridica_id", nullable = false)
    private SituacaoJuridica situacaoJuridica;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @Column(length = 100)
    private String descricaoFormaDeObtencao;

    @Column(length = 50)
    private String oficio;

    @Column(length = 50)
    private String matricula;

    @Column(length = 50)
    private String livro;

    @Column(length = 100)
    private String nomeCartorio;

    @Column(length = 50)
    private String dataRegistro;

    @Column(length = 50)
    private String numeroRegistro;

    @Column(precision = 18, scale = 4)
    private BigDecimal areaRegistrada = BigDecimal.ZERO;

    @Column(precision = 18, scale = 4)
    private BigDecimal areaMedida = BigDecimal.ZERO;

    @Column(length = 100)
    private String municipioCartorio;

    private Integer numeroHerdeiros;

    @Temporal(TemporalType.DATE)
    private Date dataPosse;

    // Validação para `descricaoFormaDeObtencao`
    public void setDescricaoFormaDeObtencao(String descricaoFormaDeObtencao) {
        if (descricaoFormaDeObtencao != null && !descricaoFormaDeObtencao.isBlank()) {
            if (!DESCRICOES_VALIDAS.contains(descricaoFormaDeObtencao)) {
                throw new IllegalArgumentException("Descrição inválida");
            }
        }
        this.descricaoFormaDeObtencao = descricaoFormaDeObtencao;
    }

    private static final Set<String> DESCRICOES_VALIDAS = Set.of(
            "01 - Aquisição do Governo Estadual", "02 - Adjudicação", "03 - Aquisição do Governo Federal",
            "04 - Aquisição INCRA", "05 - Aquisição do Governo Municipal", "06 - Carta de Arrematação",
            "07 - Compra e Venda de Particular", "08 - Concessão de Uso/Governo Estadual",
            "09 - Concessão de Uso/Governo Federal", "10 - Concessão de Uso/INCRA",
            "11 - Concessão de Uso/Municipal", "12 - Doação", "13 - Foro ou Enfiteuse",
            "14 - Incorporação", "15 - Recebimento de Herança", "16 - Usucapião",
            "17 - Usofruto", "18 - Doação em Pagamento", "19 - Desapropiação", "20 - Outras"
    );

    @Override
    public String toString() {
        return "FormaObtencao{" +
                "id=" + id +
                ", descricaoFormaDeObtencao='" + descricaoFormaDeObtencao + '\'' +
                ", areaRegistrada=" + areaRegistrada +
                ", areaMedida=" + areaMedida +
                ", numeroRegistro='" + numeroRegistro + '\'' +
                ", nomeCartorio='" + nomeCartorio + '\'' +
                ", municipioCartorio='" + municipioCartorio + '\'' +
                ", numeroHerdeiros=" + numeroHerdeiros +
                ", dataPosse=" + dataPosse +
                '}';
    }
}

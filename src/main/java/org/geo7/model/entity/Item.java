package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "item", schema = "geo7")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "geo7.item_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lote_id", nullable = false)
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "cultura_id")
    private Cultura cultura;

    @Column(name = "forma_exploracao")
    private String formaExploracao;

    @Column(name = "sequencia_produto_vegetal")
    private Integer sequenciaProdutoVegetal;

    @Column(name = "area_colhida", precision = 10, scale = 4)
    private BigDecimal areaColhida;

    @Column(name = "area_plantada", precision = 10, scale = 4)
    private BigDecimal areaPlantada;

    @Column(name = "quantidade_colhida", precision = 10, scale = 2)
    private BigDecimal quantidadeColhida;

    @ManyToOne
    @JoinColumn(name = "unidade_producao_id") // nova coluna FK
    private UnidadeProducao unidadeProducao;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "granjeira_agricola_id")
    private GranjeiraAgricola granjeiraAgricola;

    @Column(name = "area_explorada_granjeira_agricola", precision = 10, scale = 4)
    private BigDecimal areaExploradaGranjeiraAgricola;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "area_com_outro_uso_id")
    private AreaComOutroUso areaComOutroUso;

    // >>> banco tem o typo "ultilizada"
    @Column(name = "area_utilizada", precision = 10, scale = 4)
    private BigDecimal areaUtilizada;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "areas_restricoes_id")
    private AreasRestricoes areasRestricoes;

    // >>> banco tem o typo "ultilizada"
    @Column(name = "area_utilizada_restricao", precision = 10, scale = 4)
    private BigDecimal areaUtilizadaRestricao;

    @Column(name = "tipo_pastagem", length = 100)
    private String tipoPastagem;

    @Column(name = "area_pastagem", precision = 10, scale = 4)
    private BigDecimal areaPastagem;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "categoria_animal_id")
    private CategoriaAnimal categoriaAnimal;

    @Column(name = "quantidade_animal")
    private Integer quantidadeAnimal;

    // >>> banco tem o typo "ultilizada"
    @Column(name = "area_aproveitavel_nao_utilizada", precision = 10, scale = 4)
    private BigDecimal areaAproveitavelNaoUtilizada;

    @Column(name = "indicador_geral_de_restricao")
    private String indicadorGeralDeRestricao;

    @Column(name = "area_geral_item", precision = 10, scale = 4)
    private BigDecimal areaGeralItem;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "dados_sobre_uso_id")
    private DadosSobreUso dadosSobreUso;

    @Override
    public String toString() {
        return categoria != null ? categoria.toString() : "";
    }
}

package org.geo7.model.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "item", schema = "geo7")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "item_seq")
    @SequenceGenerator(name = "item_seq", sequenceName = "geo7.item_id_seq", allocationSize = 1)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "lote_id")
    private Lote lote;

    @ManyToOne
    private Categoria categoria;

    @ManyToOne
    private Cultura cultura;

    @Column
    private String formaExploracao;

    @Column
    private Integer sequenciaProdutoVegetal;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaColhida;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaPlantada;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantidadeColhida;

    @ManyToOne
    private UnidadeProducao codigoUnidadeProducao;

    @ManyToOne
    private GranjeiraAgricola granjeiraAgricola;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaExploradaGranjeiraAgricola;

    @ManyToOne
    private AreaComOutroUso areaComOutroUso;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaUltilizada;

    @ManyToOne
    private AreasRestricoes areasRestricoes;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaUltilizadaRestricao;

    @Column(nullable = false, length = 100)
    private String tipoPastagem;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaPastagem;

    @ManyToOne
    private CategoriaAnimal categoriaAnimal;

    @Column
    private Integer quantidadeAnimal;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaAproveitavelNaoUltilizada;

    @Column
    private String indicadorGeralDeRestricao;

    @Column(precision = 10, scale = 4)
    private BigDecimal areaGeralItem;

    @ManyToOne
    @JoinColumn(name = "dados_sobre_uso_id")
    private DadosSobreUso dadosSobreUso;

    @Override
    public String toString() {
        return categoria != null ? categoria.toString() : "";
    }
}

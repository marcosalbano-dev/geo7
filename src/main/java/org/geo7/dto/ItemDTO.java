package org.geo7.dto;

import org.geo7.model.entity.*;

import java.math.BigDecimal;

public record ItemDTO(
        Long id,
        Long loteId,
        Long categoriaId,
        Long culturaId,
        String formaExploracao,
        Integer sequenciaProdutoVegetal,
        BigDecimal areaColhida,
        BigDecimal areaPlantada,
        BigDecimal quantidadeColhida,

        Long unidadeProducaoId,
        String codigoUnidadeProducao,

        Long granjeiraAgricolaId,
        BigDecimal areaExploradaGranjeiraAgricola,
        Long areaComOutroUsoId,
        BigDecimal areaUtilizada,
        Long areasRestricoesId,
        BigDecimal areaUtilizadaRestricao,
        String tipoPastagem,
        BigDecimal areaPastagem,
        Long categoriaAnimalId,
        Integer quantidadeAnimal,
        BigDecimal areaAproveitavelNaoUtilizada,
        String indicadorGeralDeRestricao,
        BigDecimal areaGeralItem,
        Long dadosSobreUsoId
) {

    public static ItemDTO fromEntity(Item e) {
        return new ItemDTO(
                e.getId(),
                null != e.getLote() ? e.getLote().getId() : null,
                null != e.getCategoria() ? e.getCategoria().getId() : null,
                null != e.getCultura() ? e.getCultura().getId()   : null,
                e.getFormaExploracao(),
                e.getSequenciaProdutoVegetal(),
                e.getAreaColhida(),
                e.getAreaPlantada(),
                e.getQuantidadeColhida(),
                // preenche ambos para facilitar o front:
                null != e.getUnidadeProducao() ?e.getUnidadeProducao().getId():null,
                null != e.getUnidadeProducao() ?e.getUnidadeProducao().getCodigoUnidade():null,

                null != e.getGranjeiraAgricola() ? e.getGranjeiraAgricola().getId() : null,
                e.getAreaExploradaGranjeiraAgricola(),
                null != e.getAreaComOutroUso() ? e.getAreaComOutroUso().getId() : null,
                e.getAreaUtilizada(),
                null != e.getAreasRestricoes() ? e.getAreasRestricoes().getId() : null,
                e.getAreaUtilizadaRestricao(),
                e.getTipoPastagem(),
                e.getAreaPastagem(),
                null != e.getCategoriaAnimal() ? e.getCategoriaAnimal().getId() : null,
                e.getQuantidadeAnimal(),
                e.getAreaAproveitavelNaoUtilizada(),
                e.getIndicadorGeralDeRestricao(),
                e.getAreaGeralItem(),
                null != e.getDadosSobreUso() ? e.getDadosSobreUso().getId() : null
        );
    }

    /** Converte criando apenas referências por ID (sem hits no banco). */
    public Item toEntity() {
        Item e = new Item();
        e.setId(this.id);

        if (null != loteId) { var x = new Lote(); x.setId(this.loteId); e.setLote(x); }
        if (null != categoriaId) { var x = new Categoria(); x.setId(this.categoriaId); e.setCategoria(x); }
        if (null != culturaId) { var x = new Cultura(); x.setId(this.culturaId); e.setCultura(x); }
        if (null != unidadeProducaoId) { var x = new UnidadeProducao(); x.setId(this.unidadeProducaoId); e.setUnidadeProducao(x); }
        if (null != granjeiraAgricolaId) { var x = new GranjeiraAgricola(); x.setId(this.granjeiraAgricolaId); e.setGranjeiraAgricola(x); }
        if (null != areaComOutroUsoId) { var x = new AreaComOutroUso(); x.setId(this.areaComOutroUsoId); e.setAreaComOutroUso(x); }
        if (null != areasRestricoesId) { var x = new AreasRestricoes(); x.setId(this.areasRestricoesId); e.setAreasRestricoes(x); }
        if (null != categoriaAnimalId) { var x = new CategoriaAnimal(); x.setId(this.categoriaAnimalId); e.setCategoriaAnimal(x); }
        if (null != dadosSobreUsoId) { var x = new DadosSobreUso(); x.setId(this.dadosSobreUsoId); e.setDadosSobreUso(x); }

        e.setFormaExploracao(this.formaExploracao);
        e.setSequenciaProdutoVegetal(this.sequenciaProdutoVegetal);

        e.setAreaColhida(this.areaColhida);
        e.setAreaPlantada(this.areaPlantada);
        e.setQuantidadeColhida(this.quantidadeColhida);

        e.setAreaExploradaGranjeiraAgricola(this.areaExploradaGranjeiraAgricola);

        e.setAreaUtilizada(this.areaUtilizada);
        e.setAreaUtilizadaRestricao(this.areaUtilizadaRestricao);

        e.setTipoPastagem(this.tipoPastagem);
        e.setAreaPastagem(this.areaPastagem);

        e.setQuantidadeAnimal(this.quantidadeAnimal);
        e.setAreaAproveitavelNaoUtilizada(this.areaAproveitavelNaoUtilizada);

        e.setIndicadorGeralDeRestricao(this.indicadorGeralDeRestricao);
        e.setAreaGeralItem(this.areaGeralItem);

        return e;
    }

}

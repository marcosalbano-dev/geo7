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
                e.getLote() != null ? e.getLote().getId() : null,
                e.getCategoria() != null ? e.getCategoria().getId() : null,
                e.getCultura()   != null ? e.getCultura().getId()   : null,
                e.getFormaExploracao(),
                e.getSequenciaProdutoVegetal(),
                e.getAreaColhida(),
                e.getAreaPlantada(),
                e.getQuantidadeColhida(),
                // preenche ambos para facilitar o front:
                e.getUnidadeProducao()!=null?e.getUnidadeProducao().getId():null,
                e.getUnidadeProducao()!=null?e.getUnidadeProducao().getCodigoUnidade():null,

                e.getGranjeiraAgricola() != null ? e.getGranjeiraAgricola().getId() : null,
                e.getAreaExploradaGranjeiraAgricola(),
                e.getAreaComOutroUso() != null ? e.getAreaComOutroUso().getId() : null,
                e.getAreaUtilizada(),
                e.getAreasRestricoes() != null ? e.getAreasRestricoes().getId() : null,
                e.getAreaUtilizadaRestricao(),
                e.getTipoPastagem(),
                e.getAreaPastagem(),
                e.getCategoriaAnimal() != null ? e.getCategoriaAnimal().getId() : null,
                e.getQuantidadeAnimal(),
                e.getAreaAproveitavelNaoUtilizada(),
                e.getIndicadorGeralDeRestricao(),
                e.getAreaGeralItem(),
                e.getDadosSobreUso() != null ? e.getDadosSobreUso().getId() : null
        );
    }

    /** Converte criando apenas referências por ID (sem hits no banco). */
    public Item toEntity() {
        Item e = new Item();
        e.setId(id);

        if (loteId != null) { var x = new Lote(); x.setId(loteId); e.setLote(x); }
        if (categoriaId != null) { var x = new Categoria(); x.setId(categoriaId); e.setCategoria(x); }
        if (culturaId != null) { var x = new Cultura(); x.setId(culturaId); e.setCultura(x); }
        if (unidadeProducaoId != null) { var x = new UnidadeProducao(); x.setId(unidadeProducaoId); e.setUnidadeProducao(x); }
        if (granjeiraAgricolaId != null) { var x = new GranjeiraAgricola(); x.setId(granjeiraAgricolaId); e.setGranjeiraAgricola(x); }
        if (areaComOutroUsoId != null) { var x = new AreaComOutroUso(); x.setId(areaComOutroUsoId); e.setAreaComOutroUso(x); }
        if (areasRestricoesId != null) { var x = new AreasRestricoes(); x.setId(areasRestricoesId); e.setAreasRestricoes(x); }
        if (categoriaAnimalId != null) { var x = new CategoriaAnimal(); x.setId(categoriaAnimalId); e.setCategoriaAnimal(x); }
        if (dadosSobreUsoId != null) { var x = new DadosSobreUso(); x.setId(dadosSobreUsoId); e.setDadosSobreUso(x); }

        e.setFormaExploracao(formaExploracao);
        e.setSequenciaProdutoVegetal(sequenciaProdutoVegetal);

        e.setAreaColhida(areaColhida);
        e.setAreaPlantada(areaPlantada);
        e.setQuantidadeColhida(quantidadeColhida);

        e.setAreaExploradaGranjeiraAgricola(areaExploradaGranjeiraAgricola);

        e.setAreaUtilizada(areaUtilizada);
        e.setAreaUtilizadaRestricao(areaUtilizadaRestricao);

        e.setTipoPastagem(tipoPastagem);
        e.setAreaPastagem(areaPastagem);

        e.setQuantidadeAnimal(quantidadeAnimal);
        e.setAreaAproveitavelNaoUtilizada(areaAproveitavelNaoUtilizada);

        e.setIndicadorGeralDeRestricao(indicadorGeralDeRestricao);
        e.setAreaGeralItem(areaGeralItem);

        return e;
    }

}

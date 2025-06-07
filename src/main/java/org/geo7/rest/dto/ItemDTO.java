package org.geo7.rest.dto;

import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;


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
        String codigoUnidadeProducao,
        Long granjeiraAgricolaId,
        BigDecimal areaExploradaGranjeiraAgricola,
        Long areaComOutroUsoId,
        BigDecimal areaUltilizada,
        Long areasRestricoesId,
        BigDecimal areaUltilizadaRestricao,
        String tipoPastagem,
        BigDecimal areaPastagem,
        Long categoriaAnimalId,
        Integer quantidadeAnimal,
        BigDecimal areaAproveitavelNaoUltilizada,
        String indicadorGeralDeRestricao,
        BigDecimal areaGeralItem
) {

    public static ItemDTO fromEntity(Item entity) {
        return new ItemDTO(
                entity.getId(),
                entity.getLote() != null ? entity.getLote().getId() : null,
                entity.getCategoria() != null ? entity.getCategoria().getId() : null,
                entity.getCultura() != null ? entity.getCultura().getId() : null,
                entity.getFormaExploracao(),
                entity.getSequenciaProdutoVegetal(),
                entity.getAreaColhida(),
                entity.getAreaPlantada(),
                entity.getQuantidadeColhida(),
                entity.getCodigoUnidadeProducao() != null ? entity.getCodigoUnidadeProducao().getCodigoUnidade() : "",
                entity.getGranjeiraAgricola() != null ? entity.getGranjeiraAgricola().getId() : null,
                entity.getAreaExploradaGranjeiraAgricola(),
                entity.getAreaComOutroUso() != null ? entity.getAreaComOutroUso().getId() : null,
                entity.getAreaUltilizada(),
                entity.getAreasRestricoes() != null ? entity.getAreasRestricoes().getId() : null,
                entity.getAreaUltilizadaRestricao(),
                entity.getTipoPastagem(),
                entity.getAreaPastagem(),
                entity.getCategoriaAnimal() != null ? entity.getCategoriaAnimal().getId() : null,
                entity.getQuantidadeAnimal(),
                entity.getAreaAproveitavelNaoUltilizada(),
                entity.getIndicadorGeralDeRestricao(),
                entity.getAreaGeralItem()
        );
    }

    public Item toEntity(Categoria categoria, Lote lote, Cultura cultura, UnidadeProducao unidade, GranjeiraAgricola granjeiraAgricola,
                         AreaComOutroUso areaComOutroUso, AreasRestricoes areasRestricoes, CategoriaAnimal categoriaAnimal) {
        Item entity = new Item();
        entity.setId(id);
        entity.setLote(lote);
        entity.setCategoria(categoria);
        entity.setCultura(cultura);
        entity.setFormaExploracao(formaExploracao);
        entity.setSequenciaProdutoVegetal(sequenciaProdutoVegetal);
        entity.setAreaColhida(areaColhida);
        entity.setAreaPlantada(areaPlantada);
        entity.setQuantidadeColhida(quantidadeColhida);
        entity.setCodigoUnidadeProducao(unidade);
        entity.setGranjeiraAgricola(granjeiraAgricola);
        entity.setAreaExploradaGranjeiraAgricola(areaExploradaGranjeiraAgricola);
        entity.setAreaComOutroUso(areaComOutroUso);
        entity.setAreaUltilizada(areaUltilizada);
        entity.setAreasRestricoes(areasRestricoes);
        entity.setAreaUltilizadaRestricao(areaUltilizadaRestricao);
        entity.setTipoPastagem(tipoPastagem);
        entity.setAreaPastagem(areaPastagem);
        entity.setCategoriaAnimal(categoriaAnimal);
        entity.setQuantidadeAnimal(quantidadeAnimal);
        entity.setAreaAproveitavelNaoUltilizada(areaAproveitavelNaoUltilizada);
        entity.setIndicadorGeralDeRestricao(indicadorGeralDeRestricao);
        entity.setAreaGeralItem(areaGeralItem);
        return entity;
    }
}

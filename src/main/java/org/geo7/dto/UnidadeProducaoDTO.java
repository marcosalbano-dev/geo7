package org.geo7.dto;

import org.geo7.model.entity.UnidadeProducao;

public record UnidadeProducaoDTO(
        Long id,
        String codigoUnidade,
        String unidade
) {

    public static UnidadeProducaoDTO fromEntity(UnidadeProducao unidadeProducao) {
        return new UnidadeProducaoDTO(
                unidadeProducao.getId(),
                unidadeProducao.getCodigoUnidade(),
                unidadeProducao.getUnidade()
        );
    }

    public UnidadeProducao toEntity() {
        UnidadeProducao unidadeProducao = new UnidadeProducao();
        unidadeProducao.setId(this.id);
        unidadeProducao.setUnidade(this.unidade);
        unidadeProducao.setCodigoUnidade(this.codigoUnidade);
        return unidadeProducao;
    }
}

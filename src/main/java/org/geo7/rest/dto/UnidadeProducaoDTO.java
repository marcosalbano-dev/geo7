package org.geo7.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        unidadeProducao.setId(id);
        unidadeProducao.setUnidade(unidade);
        unidadeProducao.setCodigoUnidade(codigoUnidade);
        return unidadeProducao;
    }
}

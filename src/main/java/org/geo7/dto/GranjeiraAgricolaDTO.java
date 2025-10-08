package org.geo7.dto;

import org.geo7.model.entity.GranjeiraAgricola;

public record GranjeiraAgricolaDTO(
        Long id,
        String denominacao,
        String descricao,
        Integer codigo
) {

    public static GranjeiraAgricolaDTO fromEntity(GranjeiraAgricola entity) {
        return new GranjeiraAgricolaDTO(
                entity.getId(),
                entity.getDenominacao(),
                entity.getDescricao(),
                entity.getCodigo()
        );
    }

    public GranjeiraAgricola toEntity() {
        GranjeiraAgricola entity = new GranjeiraAgricola();
        entity.setId(this.id);
        entity.setDenominacao(this.denominacao);
        entity.setDescricao(this.descricao);
        entity.setCodigo(this.codigo);
        return entity;
    }
}


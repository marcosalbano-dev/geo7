package org.geo7.rest.dto;

import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

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
        entity.setId(id);
        entity.setDenominacao(denominacao);
        entity.setDescricao(descricao);
        entity.setCodigo(codigo);
        return entity;
    }
}


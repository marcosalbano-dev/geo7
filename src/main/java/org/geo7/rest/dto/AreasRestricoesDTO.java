package org.geo7.rest.dto;

import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public record AreasRestricoesDTO(
        Long id, String codigo, String tipoAreaRestricao
) {

    public static AreasRestricoesDTO fromEntity(AreasRestricoes entity) {
        return new AreasRestricoesDTO(
                entity.getId(), entity.getCodigo(), entity.getTipoAreaRestricao()
        );
    }

    public AreasRestricoes toEntity() {
        AreasRestricoes entity = new AreasRestricoes();
        entity.setId(id);
        entity.setCodigo(codigo);
        entity.setTipoAreaRestricao(tipoAreaRestricao);
        return entity;
    }
}

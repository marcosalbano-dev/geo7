package org.geo7.rest.mapper;

import org.geo7.model.entity.AreaComOutroUso;
import org.geo7.rest.dto.AreaComOutroUsoDTO;

public class AreaComOutroUsoMapper {

    public static AreaComOutroUsoDTO toDTO(AreaComOutroUso entity) {
        return new AreaComOutroUsoDTO(entity.getId(), entity.getCodigo(), entity.getDenominacao());
    }

    public static AreaComOutroUso toEntity(AreaComOutroUsoDTO dto) {
        AreaComOutroUso entity = new AreaComOutroUso();
        entity.setId(dto.id());
        entity.setCodigo(dto.codigo());
        entity.setDenominacao(dto.denominacao());
        return entity;
    }
}

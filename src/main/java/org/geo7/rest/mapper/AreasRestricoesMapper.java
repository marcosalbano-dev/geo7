package org.geo7.rest.mapper;


import org.geo7.model.entity.AreasRestricoes;
import org.geo7.rest.dto.AreasRestricoesDTO;

public class AreasRestricoesMapper {

    public static AreasRestricoesDTO toDTO(AreasRestricoes entity) {
        return new AreasRestricoesDTO(entity.getId(), entity.getCodigo(), entity.getTipoAreaRestricao());
    }

    public static AreasRestricoes toEntity(AreasRestricoesDTO dto) {
        AreasRestricoes entity = new AreasRestricoes();
        entity.setId(dto.id());
        entity.setCodigo(dto.codigo());
        entity.setTipoAreaRestricao(dto.tipoAreaRestricao());
        return entity;
    }
}

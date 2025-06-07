package org.geo7.rest.mapper;

import org.geo7.model.entity.SituacaoJuridica;
import org.geo7.rest.dto.SituacaoJuridicaDTO;

public class SituacaoJuridicaMapper {

    public static SituacaoJuridicaDTO toDTO(SituacaoJuridica entity) {
        return new SituacaoJuridicaDTO(
                entity.getId(),
                entity.getNome()
        );
    }

    public static SituacaoJuridica toEntity(SituacaoJuridicaDTO dto) {
        SituacaoJuridica entity = new SituacaoJuridica();
        entity.setId(dto.id());
        entity.setNome(dto.nome());
        return entity;
    }
}

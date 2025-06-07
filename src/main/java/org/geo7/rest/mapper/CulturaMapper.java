package org.geo7.rest.mapper;


import org.geo7.model.entity.Cultura;
import org.geo7.rest.dto.CulturaDTO;

public class CulturaMapper {

    public static CulturaDTO toDTO(Cultura entity) {
        return new CulturaDTO(
                entity.getId(),
                entity.getTipoCultura(),
                entity.getNomeCultura(),
                entity.getCodigoCultura()
        );
    }

    public static Cultura toEntity(CulturaDTO dto) {
        Cultura entity = new Cultura();
        entity.setId(dto.id());
        entity.setTipoCultura(dto.tipoCultura());
        entity.setNomeCultura(dto.nomeCultura());
        entity.setCodigoCultura(dto.codigoCultura());
        return entity;
    }
}

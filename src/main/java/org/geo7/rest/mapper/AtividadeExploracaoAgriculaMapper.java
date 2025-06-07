package org.geo7.rest.mapper;


import org.geo7.model.entity.AtividadeExploracaoAgricola;
import org.geo7.rest.dto.AtividadeExploracaoAgricolaDTO;

public class AtividadeExploracaoAgriculaMapper {

    public static AtividadeExploracaoAgricolaDTO toDTO(AtividadeExploracaoAgricola entity) {
        return new AtividadeExploracaoAgricolaDTO(
                entity.getId(),
                entity.getAreaExplorada(),
                entity.getIndicadorRestricao()
        );
    }

    public static AtividadeExploracaoAgricola toEntity(AtividadeExploracaoAgricolaDTO dto) {
        AtividadeExploracaoAgricola entity = new AtividadeExploracaoAgricola();
        entity.setId(dto.id());
        entity.setAreaExplorada(dto.areaExplorada());
        entity.setIndicadorRestricao(dto.indicadorRestricao());
        return entity;
    }
}

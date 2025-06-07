package org.geo7.rest.mapper;

import org.geo7.model.entity.Categoria;
import org.geo7.rest.dto.CategoriaDTO;

public class CategoriaMapper {

    public static CategoriaDTO toDTO(Categoria entity) {
        return new CategoriaDTO(entity.getId(), entity.getNomeCategoria().name());
    }

    public static Categoria toEntity(CategoriaDTO dto) {
        Categoria entity = new Categoria();
        entity.setId(dto.id());
        entity.setNomeCategoria(dto.toEntity().getNomeCategoria());
        return entity;
    }
}

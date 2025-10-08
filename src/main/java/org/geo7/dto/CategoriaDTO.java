package org.geo7.dto;

import org.geo7.model.entity.Categoria;

public record CategoriaDTO(Long id, String nomeCategoria) {
    public static CategoriaDTO fromEntity(Categoria entity) {
        return new CategoriaDTO(
                entity.getId(), entity.getNomeCategoria().name()
        );
    }

    public Categoria toEntity() {
        Categoria entity = new Categoria();
        entity.setId(this.id);
        entity.setNomeCategoria(entity.getNomeCategoria());
        return entity;
    }
}

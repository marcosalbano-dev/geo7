package org.geo7.rest.dto;

import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public record CategoriaDTO(Long id, String nomeCategoria) {
    public static CategoriaDTO fromEntity(Categoria entity) {
        return new CategoriaDTO(
                entity.getId(), entity.getNomeCategoria().name()
        );
    }

    public Categoria toEntity() {
        Categoria entity = new Categoria();
        entity.setId(id);
        entity.setNomeCategoria(entity.getNomeCategoria());
        return entity;
    }
}

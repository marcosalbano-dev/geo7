package org.geo7.dto;

import org.geo7.model.entity.CategoriaAnimal;


public record CategoriaAnimalDTO(Long id, String codigo, String denominaoCategoriaAnimal) {
    public static CategoriaAnimalDTO fromEntity(CategoriaAnimal e) {
        return new CategoriaAnimalDTO(e.getId(), e.getCodigo(), e.getDenominaoCategoriaAnimal());
    }

    public CategoriaAnimal toEntity() {
        CategoriaAnimal entity = new CategoriaAnimal();
        entity.setId(this.id);
        entity.setDenominaoCategoriaAnimal(this.denominaoCategoriaAnimal);
        entity.setCodigo(this.codigo);
        return entity;
    }
}

package org.geo7.rest.mapper;

import org.geo7.model.entity.CategoriaAnimal;
import org.geo7.rest.dto.CategoriaAnimalDTO;

public class CategoriaAnimalMapper {

    public static CategoriaAnimalDTO toDTO(CategoriaAnimal entity) {
        return new CategoriaAnimalDTO(
                entity.getId(),
                entity.getCodigo(),
                entity.getDenominaoCategoriaAnimal()
        );
    }

    public static CategoriaAnimal toEntity(CategoriaAnimalDTO dto) {
        CategoriaAnimal entity = new CategoriaAnimal();
        entity.setId(dto.id());
        entity.setCodigo(dto.codigo());
        entity.setDenominaoCategoriaAnimal(dto.denominaoCategoriaAnimal());
        return entity;
    }
}

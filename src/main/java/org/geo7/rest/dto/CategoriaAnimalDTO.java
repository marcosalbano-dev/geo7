package org.geo7.rest.dto;
import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;


public record CategoriaAnimalDTO(Long id, String denominaoCategoriaAnimal, String codigo) {
    public static CategoriaAnimalDTO fromEntity(CategoriaAnimal entity) {
        return new CategoriaAnimalDTO(
                entity.getId(), entity.getDenominaoCategoriaAnimal(), entity.getCodigo()
        );
    }

    public CategoriaAnimal toEntity() {
        CategoriaAnimal entity = new CategoriaAnimal();
        entity.setId(id);
        entity.setDenominaoCategoriaAnimal(denominaoCategoriaAnimal);
        entity.setCodigo(codigo);
        return entity;
    }
}

package org.geo7.model.repository;

import org.geo7.model.entity.Categoria;
import org.geo7.model.entity.CategoriaAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface CategoriaAnimalRepository extends JpaRepository<CategoriaAnimal, Long> {

    Optional<CategoriaAnimal> findByCodigo(String codigo);
}

package org.geo7.model.repository;

import org.geo7.model.entity.CategoriaAnimal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CategoriaAnimalRepository extends JpaRepository<CategoriaAnimal, Long> {
    List<CategoriaAnimal> findAllByOrderByIdAsc();
}

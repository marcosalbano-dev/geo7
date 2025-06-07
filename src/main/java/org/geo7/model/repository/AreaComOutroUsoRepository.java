package org.geo7.model.repository;

import org.geo7.model.entity.AreaComOutroUso;
import org.geo7.model.entity.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface AreaComOutroUsoRepository extends JpaRepository<AreaComOutroUso, Long> {

    Optional<AreaComOutroUso> findByCodigo(String codigo);
}

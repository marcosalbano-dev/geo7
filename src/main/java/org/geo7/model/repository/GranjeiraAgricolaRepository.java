package org.geo7.model.repository;

import org.geo7.model.entity.AreaComOutroUso;
import org.geo7.model.entity.GranjeiraAgricola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GranjeiraAgricolaRepository extends JpaRepository<GranjeiraAgricola, Long> {

    Optional<GranjeiraAgricola> findByCodigo(Integer codigo);
}

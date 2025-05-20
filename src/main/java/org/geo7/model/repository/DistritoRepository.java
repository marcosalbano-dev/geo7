package org.geo7.model.repository;

import org.geo7.model.entity.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DistritoRepository extends JpaRepository<Distrito, Long> {

    Optional<Distrito> findByCodigoDistrito(String codigoDistrito);
}

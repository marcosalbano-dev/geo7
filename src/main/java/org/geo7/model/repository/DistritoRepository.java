package org.geo7.model.repository;

import org.geo7.model.entity.Distrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DistritoRepository extends JpaRepository<Distrito, Long> {

    Optional<Distrito> findByCodigoDistrito(String codigoDistrito);
}

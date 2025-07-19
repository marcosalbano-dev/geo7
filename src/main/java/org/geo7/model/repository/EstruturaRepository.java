package org.geo7.model.repository;

import org.geo7.model.entity.Estrutura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EstruturaRepository extends JpaRepository<Estrutura, Long> {

    Optional<Estrutura> findByLoteId(Long loteId);

}

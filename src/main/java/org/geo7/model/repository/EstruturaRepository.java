package org.geo7.model.repository;

import org.geo7.model.entity.Estrutura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EstruturaRepository extends JpaRepository<Estrutura, Long> {
    List<Estrutura> findByLoteId(Long loteId);
}

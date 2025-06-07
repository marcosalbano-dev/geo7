package org.geo7.model.repository;

import org.geo7.model.entity.Estrutura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface EstruturaRepository extends JpaRepository<Estrutura, Long> {
    List<Estrutura> findByLoteId(Long loteId);
}

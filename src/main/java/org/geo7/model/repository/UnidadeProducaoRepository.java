package org.geo7.model.repository;

import org.geo7.model.entity.UnidadeProducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnidadeProducaoRepository extends JpaRepository<UnidadeProducao, Long> {

    Optional<UnidadeProducao> findByCodigoUnidade(String codigoUnidade);
    List<UnidadeProducao> findByUnidadeContainingIgnoreCase(String termo);
}

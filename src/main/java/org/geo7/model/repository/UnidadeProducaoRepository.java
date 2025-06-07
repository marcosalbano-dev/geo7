package org.geo7.model.repository;

import org.geo7.model.entity.Cultura;
import org.geo7.model.entity.Item;
import org.geo7.model.entity.UnidadeProducao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnidadeProducaoRepository extends JpaRepository<UnidadeProducao, Long> {

    Optional<UnidadeProducao> findByCodigoUnidade(String codigoUnidade);
}

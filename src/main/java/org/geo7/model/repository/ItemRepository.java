package org.geo7.model.repository;


import org.geo7.model.entity.Cultura;
import org.geo7.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByCodigoUnidadeProducaoCodigoUnidade(String codigoUnidade);
}

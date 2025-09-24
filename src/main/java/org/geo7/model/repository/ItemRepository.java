package org.geo7.model.repository;


import org.geo7.model.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("""
      select i from Item i
      where i.dadosSobreUso.id in :dsuIds
      """)
    List<Item> findByDadosSobreUsoIdIn(@Param("dsuIds") Collection<Long> dsuIds);

    List<Item> findByDadosSobreUsoId(Long dsuId);
}

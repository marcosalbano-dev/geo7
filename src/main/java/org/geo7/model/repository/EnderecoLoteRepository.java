package org.geo7.model.repository;

import org.geo7.model.entity.EnderecoLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoLoteRepository extends JpaRepository<EnderecoLote, Long> {

    Optional<EnderecoLote> findFirstByLote_Id(Long loteId); // <- por loteId

    @Query("""
      select el from EnderecoLote el
      where el.lote.id in :loteIds
      """)
    List<EnderecoLote> findByLoteIdIn(@Param("loteIds") Collection<Long> loteIds);

    Optional<EnderecoLote> findFirstByLoteId(Long loteId);
}

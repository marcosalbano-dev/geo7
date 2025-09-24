package org.geo7.model.repository;

import org.geo7.model.entity.FormaObtencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface FormaObtencaoRepository extends JpaRepository<FormaObtencao, Long> {

    Optional<FormaObtencao> findFirstByLoteId(Long loteId);

    Optional<FormaObtencao> findFirstByLoteIdAndSituacaoJuridicaId(Long loteId, Long situacaoJuridicaId);

    @Query("""
      select f from FormaObtencao f
      where f.lote.id in :loteIds
      order by f.id asc
      """)
    List<FormaObtencao> findByLoteIdIn(@Param("loteIds") Collection<Long> loteIds);

    List<FormaObtencao> findByLoteIdOrderByIdAsc(Long loteId);

}

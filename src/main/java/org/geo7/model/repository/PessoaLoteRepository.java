package org.geo7.model.repository;

import org.geo7.model.entity.PessoaLote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PessoaLoteRepository extends JpaRepository<PessoaLote, Long> {
    Optional<PessoaLote> findTopByLote_IdOrderByIdDesc(Long loteId);

    @Query("select pl from PessoaLote pl where pl.lote.id in :loteIds")
    List<PessoaLote> findByLoteIdIn(@Param("loteIds") Collection<Long> loteIds);

    @Query("""
      select pl from PessoaLote pl
      join fetch pl.pessoa p
      where pl.lote.id in :loteIds
      """)
    List<PessoaLote> findByLoteIdInFetchPessoa(@Param("loteIds") Collection<Long> loteIds);

    List<PessoaLote> findByLoteId(Long loteId);
}

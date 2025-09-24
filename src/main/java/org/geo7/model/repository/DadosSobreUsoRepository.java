package org.geo7.model.repository;

import org.geo7.model.entity.DadosSobreUso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DadosSobreUsoRepository extends JpaRepository<DadosSobreUso, Long> {

    Optional<DadosSobreUso> findByLoteId(Long loteId);

    @Query("""
           select d from DadosSobreUso d 
           where d.lote.id in :loteIds
           """)
    List<DadosSobreUso> findByLoteIdIn(@Param("loteIds") Collection<Long> loteIds);
}

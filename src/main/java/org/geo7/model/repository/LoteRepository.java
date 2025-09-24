package org.geo7.model.repository;

import org.geo7.model.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long>, LoteRepositoryCustom {

    @Query("SELECT DISTINCT l FROM Lote l LEFT JOIN FETCH l.formaObtencao fo LEFT JOIN FETCH l.municipio m LEFT JOIN FETCH l.situacaoJuridica sj")
    List<Lote> findAllWithFormaObtencao();

    Optional<Lote> findByProprietario(String proprietario);

    @Query("select l from Lote l where l.municipio.id = :municipioId")
    List<Lote> findByMunicipioId(@Param("municipioId") Long municipioId);

    @Query("""
      select l from Lote l
      where l.municipio.id = :municipioId
      """)
    List<Lote> findAllByMunicipioId(@Param("municipioId") Long municipioId);
}

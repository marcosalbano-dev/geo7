package org.geo7.model.repository;

import org.geo7.model.entity.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long> {

    @Query("SELECT DISTINCT l FROM Lote l LEFT JOIN FETCH l.formaObtencao fo LEFT JOIN FETCH l.municipio m LEFT JOIN FETCH l.situacaoJuridica sj")
    List<Lote> findAllWithFormaObtencao();
}

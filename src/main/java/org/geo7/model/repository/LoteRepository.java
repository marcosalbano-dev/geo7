package org.geo7.model.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.geo7.model.entity.Item;
import org.geo7.model.entity.Lote;
import org.geo7.rest.dto.LoteFiltroDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Long>, LoteRepositoryCustom {

    @Query("SELECT DISTINCT l FROM Lote l LEFT JOIN FETCH l.formaObtencao fo LEFT JOIN FETCH l.municipio m LEFT JOIN FETCH l.situacaoJuridica sj")
    List<Lote> findAllWithFormaObtencao();

    Optional<Lote> findByProprietario(String proprietario);

}

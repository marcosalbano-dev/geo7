package org.geo7.model.repository;

import org.geo7.model.entity.FormaObtencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FormaObtencaoRepository extends JpaRepository<FormaObtencao, Long> {

    Optional<FormaObtencao> findFirstByLoteId(Long loteId);

    Optional<FormaObtencao> findFirstByLoteIdAndSituacaoJuridicaId(Long loteId, Long situacaoJuridicaId);

}

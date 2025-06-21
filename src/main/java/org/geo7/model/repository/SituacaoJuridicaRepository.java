package org.geo7.model.repository;

import org.geo7.model.entity.SituacaoJuridica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SituacaoJuridicaRepository extends JpaRepository<SituacaoJuridica, Long> {

    Optional<SituacaoJuridica> findByNome(String nome);
}

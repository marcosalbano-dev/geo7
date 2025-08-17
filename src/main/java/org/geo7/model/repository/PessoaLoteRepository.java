package org.geo7.model.repository;

import org.geo7.model.entity.PessoaLote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PessoaLoteRepository extends JpaRepository<PessoaLote, Long> {
    Optional<PessoaLote> findTopByLote_IdOrderByIdDesc(Long loteId);
}

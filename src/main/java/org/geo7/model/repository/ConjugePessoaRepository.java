package org.geo7.model.repository;

import org.geo7.model.entity.ConjugePessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConjugePessoaRepository extends JpaRepository<ConjugePessoa, Long> {

    List<ConjugePessoa> findByPessoaId(Long pessoaId);
}

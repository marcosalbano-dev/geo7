package org.geo7.model.repository;
import org.geo7.model.entity.DocumentoPessoa;
import org.geo7.model.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DocumentoPessoaRepository extends JpaRepository<DocumentoPessoa, Long> {
    DocumentoPessoa findByPessoa(Pessoa pessoa);
    Optional<DocumentoPessoa> findFirstByPessoaId(Long pessoaId);
}

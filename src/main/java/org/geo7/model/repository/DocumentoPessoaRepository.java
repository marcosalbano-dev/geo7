package org.geo7.model.repository;
import org.geo7.model.entity.DocumentoPessoa;
import org.geo7.model.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentoPessoaRepository extends JpaRepository<DocumentoPessoa, Long> {
    DocumentoPessoa findByPessoa(Pessoa pessoa);
}

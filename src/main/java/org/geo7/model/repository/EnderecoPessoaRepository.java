package org.geo7.model.repository;

import org.geo7.model.entity.EnderecoPessoa;
import org.geo7.model.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnderecoPessoaRepository extends JpaRepository<EnderecoPessoa, Long> {
    EnderecoPessoa findByPessoa(Pessoa pessoa);
}



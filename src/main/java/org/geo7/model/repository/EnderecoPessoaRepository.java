package org.geo7.model.repository;

import org.geo7.model.entity.EnderecoPessoa;
import org.geo7.model.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EnderecoPessoaRepository extends JpaRepository<EnderecoPessoa, Long> {
    EnderecoPessoa findByPessoa(Pessoa pessoa);
    Optional<EnderecoPessoa> findFirstByPessoaId(Long pessoaId);

}



package org.geo7.model.repository;

import org.geo7.model.entity.DocumentoPessoa;
import org.geo7.model.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentoPessoaRepository extends JpaRepository<DocumentoPessoa, Long> {

    DocumentoPessoa findByPessoa(Pessoa pessoa);
    Optional<DocumentoPessoa> findFirstByPessoaId(Long pessoaId);

    @Query("""
      select dp from DocumentoPessoa dp
      where dp.pessoa.id in :pessoaIds
      """)
    List<DocumentoPessoa> findByPessoaIdIn(@Param("pessoaIds") Collection<Long> pessoaIds);

}

package org.geo7.model.repository;

import org.geo7.model.entity.EnderecoPessoa;
import org.geo7.model.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface EnderecoPessoaRepository extends JpaRepository<EnderecoPessoa, Long> {

    EnderecoPessoa findByPessoa(Pessoa pessoa);
    Optional<EnderecoPessoa> findFirstByPessoaId(Long pessoaId);

    @Query("""
      select ep from EnderecoPessoa ep
      where ep.pessoa.id in :pessoaIds
      """)
    List<EnderecoPessoa> findByPessoaIdIn(@Param("pessoaIds") Collection<Long> pessoaIds);

}



package org.geo7.model.repository;

import org.geo7.model.entity.AtividadeExploracaoAgricola;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AtividadeExploracaoAgricolaRepository extends JpaRepository<AtividadeExploracaoAgricola, Long> {
}

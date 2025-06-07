package org.geo7.model.repository;

import org.geo7.model.entity.DadosSobreUso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DadosSobreUsoRepository extends JpaRepository<DadosSobreUso, Long> {
}

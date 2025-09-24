package org.geo7.model.repository;

import org.geo7.model.entity.Cultura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CulturaRepository extends JpaRepository<Cultura, Long> {
    List<Cultura> findAllByOrderByNomeCulturaAsc();
}

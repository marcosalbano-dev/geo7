package org.geo7.model.repository;

import org.geo7.model.entity.Municipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MunicipioRepository extends JpaRepository<Municipio, Long> {

    List<Municipio> findByUfOrderByNomeAsc(String uf);

//
//    @Query("SELECT DISTINCT m.uf FROM Municipio m ORDER BY m.uf")
//    List<String> findDistinctUfs();

}

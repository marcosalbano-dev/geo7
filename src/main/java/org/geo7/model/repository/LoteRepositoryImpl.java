package org.geo7.model.repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.*;
import org.geo7.model.entity.Lote;
import org.geo7.rest.dto.LoteFiltroDTO;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class LoteRepositoryImpl implements LoteRepositoryCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Lote> filtrarLotes(LoteFiltroDTO filtro) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Lote> cq = cb.createQuery(Lote.class);
        Root<Lote> lote = cq.from(Lote.class);

        List<Predicate> predicates = new ArrayList<>();

        if (filtro.getProprietario() != null && !filtro.getProprietario().isEmpty()) {
            predicates.add(cb.like(cb.lower(lote.get("proprietario")), "%" + filtro.getProprietario().toLowerCase() + "%"));
        }
        if (filtro.getCpf() != null && !filtro.getCpf().isEmpty()) {
            predicates.add(cb.equal(lote.get("cpf"), filtro.getCpf()));
        }
        if (filtro.getNumero() != null && !filtro.getNumero().isEmpty()) {
            predicates.add(cb.equal(lote.get("numero"), filtro.getNumero()));
        }
        if (filtro.getMunicipioId() != null) {
            predicates.add(cb.equal(lote.get("municipio").get("id"), filtro.getMunicipioId()));
        }
        if (filtro.getDenominacaoImovel() != null && !filtro.getDenominacaoImovel().isEmpty()) {
            predicates.add(cb.like(cb.lower(lote.get("denominacaoImovel")), "%" + filtro.getDenominacaoImovel().toLowerCase() + "%"));
        }

        cq.where(predicates.toArray(new Predicate[0]));
        return entityManager.createQuery(cq).getResultList();
    }
}

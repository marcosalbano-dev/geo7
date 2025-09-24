package org.geo7.model.repository;

import org.geo7.model.entity.Lote;
import org.geo7.dto.LoteFiltroDTO;

import java.util.List;

public interface LoteRepositoryCustom {

    List<Lote> filtrarLotes(LoteFiltroDTO filtro);
}

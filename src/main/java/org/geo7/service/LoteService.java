package org.geo7.service;

import org.geo7.model.repository.LoteRepository;
import org.geo7.rest.dto.LoteDTO;
import org.geo7.rest.dto.LoteFiltroDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoteService {
    private final LoteRepository loteRepository;

    public LoteService(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    public List<LoteDTO> filtrarLotes(LoteFiltroDTO filtro) {
        return loteRepository.filtrarLotes(filtro)
                .stream()
                .map(LoteDTO::fromEntity)
                .toList();
    }
}

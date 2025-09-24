package org.geo7.service;

import lombok.RequiredArgsConstructor;
import org.geo7.dto.ItemDTO;
import org.geo7.model.entity.UnidadeProducao;
import org.geo7.model.repository.UnidadeProducaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class ItemService {

    @Autowired UnidadeProducaoRepository unidadeProducaoRepository;

    private UnidadeProducao resolveUnidade(ItemDTO dto){
        if (dto.unidadeProducaoId() != null) {
            return unidadeProducaoRepository.findById(dto.unidadeProducaoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Unidade de produção (ID) inválida"));
        }
        if (dto.codigoUnidadeProducao() != null && !dto.codigoUnidadeProducao().isBlank()) {
            return unidadeProducaoRepository.findByCodigoUnidade(dto.codigoUnidadeProducao().trim())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST, "Código de unidade inválido"));
        }
        return null; // campo opcional
    }

}

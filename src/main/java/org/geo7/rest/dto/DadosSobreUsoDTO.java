package org.geo7.rest.dto;

import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public record DadosSobreUsoDTO(Long id, Double areaTotalRotacao, Double areaTotalConsorcio, Double areaTotalIsolado) {

    public static DadosSobreUsoDTO fromEntity(DadosSobreUso entity) {
        return new DadosSobreUsoDTO(
                entity.getId(), entity.getAreaTotalRotacao(), entity.getAreaTotalConsorcio(), entity.getAreaTotalIsolado()
        );
    }

    public DadosSobreUso toEntity() {
        DadosSobreUso entity = new DadosSobreUso();
        entity.setId(id);
        entity.setAreaTotalRotacao(areaTotalRotacao);
        entity.setAreaTotalConsorcio(areaTotalConsorcio);
        entity.setAreaTotalIsolado(areaTotalIsolado);
        return entity;
    }
}

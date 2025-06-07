package org.geo7.rest.dto;

import org.geo7.enums.TipoCultura;
import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.stream.Collectors;

public record CulturaDTO(Long id, TipoCultura tipoCultura, String nomeCultura, Integer codigoCultura) {
    public static CulturaDTO fromEntity(Cultura entity) {
        return new CulturaDTO(
                entity.getId(), entity.getTipoCultura(), entity.getNomeCultura(), entity.getCodigoCultura()
        );
    }

    public Cultura toEntity() {
        Cultura entity = new Cultura();
        entity.setId(id);
        entity.setTipoCultura(tipoCultura);
        entity.setNomeCultura(nomeCultura);
        entity.setCodigoCultura(codigoCultura);
        return entity;
    }
}

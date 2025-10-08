package org.geo7.dto;

import org.geo7.enums.TipoCultura;
import org.geo7.model.entity.Cultura;

public record CulturaDTO(Long id, Integer codigoCultura, String nomeCultura, TipoCultura tipoCultura) {
    public static CulturaDTO fromEntity(Cultura c) {
        return new CulturaDTO(c.getId(), c.getCodigoCultura(), c.getNomeCultura(), c.getTipoCultura());
    }

    public Cultura toEntity() {
        Cultura entity = new Cultura();
        entity.setId(this.id);
        entity.setTipoCultura(this.tipoCultura);
        entity.setNomeCultura(this.nomeCultura);
        entity.setCodigoCultura(this.codigoCultura);
        return entity;
    }
}

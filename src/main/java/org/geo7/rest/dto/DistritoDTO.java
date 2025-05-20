package org.geo7.rest.dto;

import org.geo7.model.entity.Distrito;
import org.geo7.model.entity.Municipio;

public record DistritoDTO(
        Long id,
        String codigoDistrito,
        String nomeDistrito,
        Long municipioId
) {
    public static DistritoDTO fromEntity(Distrito d) {
        return new DistritoDTO(
                d.getId(),
                d.getCodigoDistrito(),
                d.getNomeDistrito(),
                d.getMunicipio() != null ? d.getMunicipio().getId() : null
        );
    }

    public Distrito toEntity(Municipio municipio) {
        Distrito d = new Distrito();
        d.setId(id);
        d.setCodigoDistrito(codigoDistrito);
        d.setNomeDistrito(nomeDistrito);
        d.setMunicipio(municipio);
        return d;
    }
}

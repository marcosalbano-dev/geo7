package org.geo7.dto;


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
                null != d.getMunicipio() ? d.getMunicipio().getId() : null
        );
    }

    public Distrito toEntity(Municipio municipio) {
        Distrito d = new Distrito();
        d.setId(this.id);
        d.setCodigoDistrito(this.codigoDistrito);
        d.setNomeDistrito(this.nomeDistrito);
        d.setMunicipio(municipio);
        return d;
    }
}

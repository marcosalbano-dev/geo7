package org.geo7.rest.dto;

import org.geo7.model.entity.Municipio;

public record MunicipioDTO(
        Long id,
        String nome,
        String uf,
        String regiao,
        String microregiao,
        Double latitude,
        Double longitude,
        Integer areaModuloFiscal,
        String mesoregiao
) {
    public static MunicipioDTO fromEntity(Municipio m) {
        return new MunicipioDTO(
                m.getId(),
                m.getNome(),
                m.getUf(),
                m.getRegiao(),
                m.getMicroregiao(),
                m.getLatitude(),
                m.getLongitude(),
                m.getAreaModuloFiscal(),
                m.getMesoregiao()
        );
    }

    public Municipio toEntity() {
        Municipio m = new Municipio();
        m.setId(id);
        m.setNome(nome);
        m.setUf(uf);
        m.setRegiao(regiao);
        m.setMicroregiao(microregiao);
        m.setLatitude(latitude);
        m.setLongitude(longitude);
        m.setAreaModuloFiscal(areaModuloFiscal);
        m.setMesoregiao(mesoregiao);
        return m;
    }
}
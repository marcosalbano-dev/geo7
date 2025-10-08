package org.geo7.dto;

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
        m.setId(this.id);
        m.setNome(this.nome);
        m.setUf(this.uf);
        m.setRegiao(this.regiao);
        m.setMicroregiao(this.microregiao);
        m.setLatitude(this.latitude);
        m.setLongitude(this.longitude);
        m.setAreaModuloFiscal(this.areaModuloFiscal);
        m.setMesoregiao(this.mesoregiao);
        return m;
    }
}
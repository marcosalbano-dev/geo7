package org.geo7.dto;

import org.geo7.model.entity.AreasRestricoes;

public record AreasRestricoesDTO(Long id, String codigo, String tipoAreaRestricao) {
    public static AreasRestricoesDTO fromEntity(AreasRestricoes e) {
        return new AreasRestricoesDTO(e.getId(), e.getCodigo(), e.getTipoAreaRestricao());
    }

    public AreasRestricoes toEntity() {
        AreasRestricoes entity = new AreasRestricoes();
        entity.setId(this.id);
        entity.setCodigo(this.codigo);
        entity.setTipoAreaRestricao(this.tipoAreaRestricao);
        return entity;
    }
}

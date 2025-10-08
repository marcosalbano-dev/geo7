package org.geo7.dto;

import org.geo7.model.entity.AreaComOutroUso;


public record AreaComOutroUsoDTO(Long id, String codigo, String denominacao) {
    public static AreaComOutroUsoDTO fromEntity(AreaComOutroUso e) {
        return new AreaComOutroUsoDTO(e.getId(), e.getCodigo(), e.getDenominacao());
    }

    public AreaComOutroUso toEntity() {
        AreaComOutroUso entity = new AreaComOutroUso();
        entity.setId(this.id);
        entity.setDenominacao(this.denominacao);
        entity.setCodigo(this.codigo);
        return entity;
    }
}

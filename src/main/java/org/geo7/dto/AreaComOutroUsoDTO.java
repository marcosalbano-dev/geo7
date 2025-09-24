package org.geo7.dto;

import org.geo7.model.entity.AreaComOutroUso;


public record AreaComOutroUsoDTO(Long id, String codigo, String denominacao) {
    public static AreaComOutroUsoDTO fromEntity(AreaComOutroUso e) {
        return new AreaComOutroUsoDTO(e.getId(), e.getCodigo(), e.getDenominacao());
    }

    public AreaComOutroUso toEntity() {
        AreaComOutroUso entity = new AreaComOutroUso();
        entity.setId(id);
        entity.setDenominacao(denominacao);
        entity.setCodigo(codigo);
        return entity;
    }
}

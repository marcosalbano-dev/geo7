package org.geo7.rest.dto;
import org.geo7.model.entity.*;


public record AreaComOutroUsoDTO(
        Long id, String denominacao, String codigo
) {

    public static AreaComOutroUsoDTO fromEntity(AreaComOutroUso entity) {
        return new AreaComOutroUsoDTO(
                entity.getId(), entity.getDenominacao(), entity.getCodigo()
        );
    }

    public AreaComOutroUso toEntity() {
        AreaComOutroUso entity = new AreaComOutroUso();
        entity.setId(id);
        entity.setDenominacao(denominacao);
        entity.setCodigo(codigo);
        return entity;
    }
}

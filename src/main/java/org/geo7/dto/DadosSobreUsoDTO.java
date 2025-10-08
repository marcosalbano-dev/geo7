// src/main/java/org/geo7/dto/DadosSobreUsoDTO.java
package org.geo7.dto;

import org.geo7.model.entity.DadosSobreUso;
import org.geo7.model.entity.Lote;
// Se o seu ItemDTO estiver em outro pacote, ajuste o import:

import java.util.List;

public record DadosSobreUsoDTO(
        Long id,
        Long loteId,
        Double areaTotalIsolado,
        Double areaTotalConsorcio,
        Double areaTotalRotacao,
        List<ItemDTO> items
) {
    public static DadosSobreUsoDTO fromEntity(DadosSobreUso e) {
        return new DadosSobreUsoDTO(
                e.getId(),
                null != e.getLote() ? e.getLote().getId() : null,
                e.getAreaTotalIsolado(),
                e.getAreaTotalConsorcio(),
                e.getAreaTotalRotacao(),
                null == e.getItems() ? List.of()
                        : e.getItems().stream().map(ItemDTO::fromEntity).toList()
        );
    }

    /**
     * Conversão "rasinha", útil se quiser reaproveitar em serviços.
     * O controller já está tratando lote/itens com mais cuidado,
     * então este método é opcional.
     */
    public DadosSobreUso toEntityShallow() {
        var d = new DadosSobreUso();
        d.setId(this.id);
        d.setAreaTotalIsolado(DadosSobreUsoDTO.z(this.areaTotalIsolado));
        d.setAreaTotalConsorcio(DadosSobreUsoDTO.z(this.areaTotalConsorcio));
        d.setAreaTotalRotacao(DadosSobreUsoDTO.z(this.areaTotalRotacao));
        if (null != loteId) {
            var l = new Lote();
            l.setId(this.loteId);
            d.setLote(l);
        }
        return d;
    }

    private static Double z(Double v) { return null == v ? 0.0d : v; }
}

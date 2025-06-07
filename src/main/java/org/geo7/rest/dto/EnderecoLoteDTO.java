package org.geo7.rest.dto;

import org.geo7.model.entity.Distrito;
import org.geo7.model.entity.EnderecoLote;
import org.geo7.model.entity.Lote;

import java.math.BigDecimal;
import java.util.Date;

public record EnderecoLoteDTO(
        Long id,
        Long loteId,
        Boolean ativo,
        Date dhc,
        Date dhm,
        String pontoDeReferencia,
        String codImoReceita,
        BigDecimal areaUrbana,
        Long distritoId,
        String comunidade,
        String localidade
) {
    public static EnderecoLoteDTO fromEntity(EnderecoLote enderecoLote) {
        return new EnderecoLoteDTO(
                enderecoLote.getId(),
                enderecoLote.getLote() != null ? enderecoLote.getLote().getId() : null,
                enderecoLote.getAtivo(),
                enderecoLote.getDhc(),
                enderecoLote.getDhm(),
                enderecoLote.getPontoDeReferencia(),
                enderecoLote.getCodImoReceita(),
                enderecoLote.getAreaUrbana(),
                enderecoLote.getDistrito() != null ? enderecoLote.getDistrito().getId() : null,
                enderecoLote.getComunidade(),
                enderecoLote.getLocalidade()
        );
    }

    /**
     * Converte o DTO para entidade. As entidades relacionadas (Lote e Distrito) são instanciadas apenas com o ID.
     * O serviço deve garantir que os IDs são válidos antes de persistir.
     */
    public EnderecoLote toEntity() {
        EnderecoLote enderecoLote = new EnderecoLote();
        enderecoLote.setId(this.id());

        if (this.loteId() != null) {
            Lote lote = new Lote();
            lote.setId(this.loteId());
            enderecoLote.setLote(lote);
        }

        enderecoLote.setAtivo(this.ativo() != null ? this.ativo() : true);
        enderecoLote.setDhc(this.dhc() != null ? this.dhc() : new Date());
        enderecoLote.setDhm(this.dhm() != null ? this.dhm() : new Date());
        enderecoLote.setPontoDeReferencia(this.pontoDeReferencia());
        enderecoLote.setCodImoReceita(this.codImoReceita());
        enderecoLote.setAreaUrbana(this.areaUrbana() != null ? this.areaUrbana() : BigDecimal.ZERO);

        if (this.distritoId() != null) {
            Distrito distrito = new Distrito();
            distrito.setId(this.distritoId());
            enderecoLote.setDistrito(distrito);
        }

        enderecoLote.setComunidade(this.comunidade());
        enderecoLote.setLocalidade(this.localidade());

        return enderecoLote;
    }
}

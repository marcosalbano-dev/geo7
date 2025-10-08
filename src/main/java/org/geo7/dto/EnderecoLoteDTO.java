package org.geo7.dto;

import org.geo7.model.entity.Distrito;
import org.geo7.model.entity.EnderecoLote;
import org.geo7.model.entity.Lote;

import java.io.Serializable;
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
) implements Serializable {
    public static EnderecoLoteDTO fromEntity(EnderecoLote enderecoLote) {
        return new EnderecoLoteDTO(
                enderecoLote.getId(),
                null != enderecoLote.getLote() ? enderecoLote.getLote().getId() : null,
                enderecoLote.getAtivo(),
                enderecoLote.getDhc(),
                enderecoLote.getDhm(),
                enderecoLote.getPontoDeReferencia(),
                enderecoLote.getCodImoReceita(),
                enderecoLote.getAreaUrbana(),
                null != enderecoLote.getDistrito() ? enderecoLote.getDistrito().getId() : null,
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
        enderecoLote.setId(id());

        if (null != this.loteId()) {
            Lote lote = new Lote();
            lote.setId(loteId());
            enderecoLote.setLote(lote);
        }

        enderecoLote.setAtivo(null != this.ativo() ? ativo() : true);
        enderecoLote.setDhc(null != this.dhc() ? dhc() : new Date());
        enderecoLote.setDhm(null != this.dhm() ? dhm() : new Date());
        enderecoLote.setPontoDeReferencia(pontoDeReferencia());
        enderecoLote.setCodImoReceita(codImoReceita());
        enderecoLote.setAreaUrbana(null != this.areaUrbana() ? areaUrbana() : BigDecimal.ZERO);

        if (null != this.distritoId()) {
            Distrito distrito = new Distrito();
            distrito.setId(distritoId());
            enderecoLote.setDistrito(distrito);
        }

        enderecoLote.setComunidade(comunidade());
        enderecoLote.setLocalidade(localidade());

        return enderecoLote;
    }
}

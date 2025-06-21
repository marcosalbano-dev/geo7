package org.geo7.rest.dto;

import org.geo7.model.entity.PessoaLote;
import java.math.BigDecimal;
import java.util.Date;

public record PessoaLoteDTO(
        Long id,
        Long pessoaId,
        Long loteId,
        Date dateCreated,
        Date lastUpdated,
        String codigoImovelRural,
        String condicaoPessoaImovelRural,
        BigDecimal percentDetencao,
        Boolean isDeclarante,
        Boolean isResideNoImovel,
        String tipoDoAto,
        Double numeroAto,
        Date dataAto,
        BigDecimal quantidadeAreaCedida,
        String atividadePrincipalExploracao,
        String contrato,
        Date dataTerminoContrato,
        Boolean isContratoPrazoIndeterminado
) {
    public static PessoaLoteDTO fromEntity(PessoaLote pl) {
        return new PessoaLoteDTO(
                pl.getId(),
                pl.getPessoa() != null ? pl.getPessoa().getId() : null,
                pl.getLote() != null ? pl.getLote().getId() : null,
                pl.getDateCreated(),
                pl.getLastUpdated(),
                pl.getCodigoImovelRural(),
                pl.getCondicaoPessoaImovelRural(),
                pl.getPercentDetencao(),
                pl.getIsDeclarante(),
                pl.getIsResideNoImovel(),
                pl.getTipoDoAto(),
                pl.getNumeroAto(),
                pl.getDataAto(),
                pl.getQuantidadeAreaCedida(),
                pl.getAtividadePrincipalExploracao(),
                pl.getContrato(),
                pl.getDataTerminoContrato(),
                pl.getIsContratoPrazoIndeterminado()
        );
    }
}

package org.geo7.rest.dto;

import org.geo7.model.entity.Lote;
import org.geo7.model.entity.Pessoa;
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

    public PessoaLote toEntity(Pessoa pessoa, Lote lote) {
        return PessoaLote.builder()
                .id(this.id)
                .pessoa(pessoa)
                .lote(lote)
                .dateCreated(this.dateCreated)
                .lastUpdated(this.lastUpdated)
                .codigoImovelRural(this.codigoImovelRural)
                .condicaoPessoaImovelRural(this.condicaoPessoaImovelRural)
                .percentDetencao(this.percentDetencao)
                .isDeclarante(this.isDeclarante)
                .isResideNoImovel(this.isResideNoImovel)
                .tipoDoAto(this.tipoDoAto)
                .numeroAto(this.numeroAto)
                .dataAto(this.dataAto)
                .quantidadeAreaCedida(this.quantidadeAreaCedida)
                .atividadePrincipalExploracao(this.atividadePrincipalExploracao)
                .contrato(this.contrato)
                .dataTerminoContrato(this.dataTerminoContrato)
                .isContratoPrazoIndeterminado(this.isContratoPrazoIndeterminado)
                .build();
    }
}

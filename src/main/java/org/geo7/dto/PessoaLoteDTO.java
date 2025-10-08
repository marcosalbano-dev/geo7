package org.geo7.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date dataAto,
        BigDecimal quantidadeAreaCedida,
        String atividadePrincipalExploracao,
        String contrato,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date dataTerminoContrato,
        Boolean isContratoPrazoIndeterminado
) {
    public static PessoaLoteDTO fromEntity(PessoaLote pl) {
        return new PessoaLoteDTO(
                pl.getId(),
                null != pl.getPessoa() ? pl.getPessoa().getId() : null,
                null != pl.getLote() ? pl.getLote().getId() : null,
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
                .id(id)
                .pessoa(pessoa)
                .lote(lote)
                .dateCreated(dateCreated)
                .lastUpdated(lastUpdated)
                .codigoImovelRural(codigoImovelRural)
                .condicaoPessoaImovelRural(condicaoPessoaImovelRural)
                .percentDetencao(percentDetencao)
                .isDeclarante(isDeclarante)
                .isResideNoImovel(isResideNoImovel)
                .tipoDoAto(tipoDoAto)
                .numeroAto(numeroAto)
                .dataAto(dataAto)
                .quantidadeAreaCedida(quantidadeAreaCedida)
                .atividadePrincipalExploracao(atividadePrincipalExploracao)
                .contrato(contrato)
                .dataTerminoContrato(dataTerminoContrato)
                .isContratoPrazoIndeterminado(isContratoPrazoIndeterminado)
                .build();
    }
}

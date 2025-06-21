package org.geo7.rest.dto;

import org.geo7.model.entity.Pessoa;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public record PessoaDTO(
        Long id,
        String nome,
        String telefone,
        String fax,
        String ramal,
        String email,
        String nomePai,
        String nomeMae,
        Date dataNascimento,
        String sexoPessoa,
        Boolean isEspolio,
        String codigoPessoaIncra,
        String coordenadaEste,
        String coordenadaNorte,
        String atividadePrincipal,
        String regimeDeBens,
        String dataCasamento,
        Boolean isRecebePronaf,
        Boolean isRecebeAjudoProgramaGoverno,
        Integer qtdPronaf,
        BigDecimal valorTotalPronafs,
        String racaCor,
        Set<Long> programasDoGovernoIds, // IDs dos programas
        Set<Long> pronafIds // IDs dos pronafs
) {
    public static PessoaDTO fromEntity(Pessoa p) {
        return new PessoaDTO(
                p.getId(),
                p.getNome(),
                p.getTelefone(),
                p.getFax(),
                p.getRamal(),
                p.getEmail(),
                p.getNomePai(),
                p.getNomeMae(),
                p.getDataNascimento(),
                p.getSexoPessoa(),
                p.getIsEspolio(),
                p.getCodigoPessoaIncra(),
                p.getCoordenadaEste(),
                p.getCoordenadaNorte(),
                p.getAtividadePrincipal(),
                p.getRegimeDeBens(),
                p.getDataCasamento(),
                p.getIsRecebePronaf(),
                p.getIsRecebeAjudoProgramaGoverno(),
                p.getQtdPronaf(),
                p.getValorTotalPronafs(),
                p.getRacaCor(),
                p.getProgramasDoGoverno() != null ?
                        p.getProgramasDoGoverno().stream().map(pg -> pg.getId()).collect(Collectors.toSet()) : null,
                p.getPronafs() != null ?
                        p.getPronafs().stream().map(pr -> pr.getId()).collect(Collectors.toSet()) : null
        );
    }
}

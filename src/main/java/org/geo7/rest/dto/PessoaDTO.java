package org.geo7.rest.dto;

import org.geo7.model.entity.Pessoa;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

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

        // Anexo
        String codigoPessoaIncra,
        String coordenadaEste,
        String coordenadaNorte,
        String atividadePrincipal,

        // Cônjuge e outros
        String regimeDeBens,
        String dataCasamento,

        Boolean isRecebePronaf,
        Boolean isRecebeAjudoProgramaGoverno,
        Integer qtdPronaf,
        BigDecimal valorTotalPronafs,

        String racaCor,

        // Relacionamentos por ID (opcional, implemente depois se precisar)
        Set<Long> programasDoGovernoIds, // Exemplo, se quiser retornar IDs dos programas

        Set<Long> pronafsIds // Exemplo, se quiser retornar IDs dos pronafs
) {
    public static PessoaDTO fromEntity(Pessoa pessoa) {
        // Se precisar retornar os IDs dos programas do governo:
        Set<Long> pronafsIds = null;
        if (pessoa.getPronafs() != null) {
            pronafsIds = pessoa.getPronafs()
                    .stream()
                    .map(pg -> pg.getId())
                    .collect(java.util.stream.Collectors.toSet());
        }

        Set<Long> programasIds = null;
        if (pessoa.getProgramasDoGoverno() != null) {
            programasIds = pessoa.getProgramasDoGoverno()
                    .stream()
                    .map(pg -> pg.getId())
                    .collect(java.util.stream.Collectors.toSet());
        }
        return new PessoaDTO(
                pessoa.getId(),
                pessoa.getNome(),
                pessoa.getTelefone(),
                pessoa.getFax(),
                pessoa.getRamal(),
                pessoa.getEmail(),
                pessoa.getNomePai(),
                pessoa.getNomeMae(),
                pessoa.getDataNascimento(),
                pessoa.getSexoPessoa(),
                pessoa.getIsEspolio(),
                pessoa.getCodigoPessoaIncra(),
                pessoa.getCoordenadaEste(),
                pessoa.getCoordenadaNorte(),
                pessoa.getAtividadePrincipal(),
                pessoa.getRegimeDeBens(),
                pessoa.getDataCasamento(),
                pessoa.getIsRecebePronaf(),
                pessoa.getIsRecebeAjudoProgramaGoverno(),
                pessoa.getQtdPronaf(),
                pessoa.getValorTotalPronafs(),
                pessoa.getRacaCor(),
                programasIds,
                pronafsIds
        );
    }

    public Pessoa toEntity() {
        Pessoa pessoa = new Pessoa();
        pessoa.setId(id);
        pessoa.setNome(nome);
        pessoa.setTelefone(telefone);
        pessoa.setFax(fax);
        pessoa.setRamal(ramal);
        pessoa.setEmail(email);
        pessoa.setNomePai(nomePai);
        pessoa.setNomeMae(nomeMae);
        pessoa.setDataNascimento(dataNascimento);
        pessoa.setSexoPessoa(sexoPessoa);
        pessoa.setIsEspolio(isEspolio != null ? isEspolio : false);
        pessoa.setCodigoPessoaIncra(codigoPessoaIncra);
        pessoa.setCoordenadaEste(coordenadaEste);
        pessoa.setCoordenadaNorte(coordenadaNorte);
        pessoa.setAtividadePrincipal(atividadePrincipal);
        pessoa.setRegimeDeBens(regimeDeBens);
        pessoa.setDataCasamento(dataCasamento);
        pessoa.setIsRecebePronaf(isRecebePronaf != null ? isRecebePronaf : false);
        pessoa.setIsRecebeAjudoProgramaGoverno(isRecebeAjudoProgramaGoverno != null ? isRecebeAjudoProgramaGoverno : false);
        pessoa.setQtdPronaf(qtdPronaf != null ? qtdPronaf : 0);
        pessoa.setValorTotalPronafs(valorTotalPronafs != null ? valorTotalPronafs : BigDecimal.ZERO);
        pessoa.setRacaCor(racaCor);
        // **Relacionamentos:**
        // Aqui você pode popular programasDoGoverno via Service/Repository no Controller se precisar.
        return pessoa;
    }
}

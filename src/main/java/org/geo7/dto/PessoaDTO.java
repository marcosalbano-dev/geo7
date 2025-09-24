package org.geo7.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.geo7.model.entity.Pessoa;
import org.geo7.model.entity.Pronaf;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
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
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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

        Set<Long> pronafsIds // Exemplo, se quiser retornar IDs dos pronafs
) {
    public static PessoaDTO fromEntity(Pessoa pessoa) {
        var pronafs = Optional.ofNullable(pessoa.getPronafs()).orElseGet(Set::of);

        Set<Long> pronafsIds = pronafs.stream()
                .filter(Objects::nonNull)
                .map(Pronaf::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        BigDecimal totalPronafs = Optional.ofNullable(pessoa.getValorTotalPronafs())
                .orElse(BigDecimal.ZERO);

        Integer qtdPronaf = Optional.ofNullable(pessoa.getQtdPronaf())
                .orElse(pronafsIds.size());

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
                Optional.ofNullable(pessoa.getIsEspolio()).orElse(false),

                pessoa.getCodigoPessoaIncra(),
                pessoa.getCoordenadaEste(),
                pessoa.getCoordenadaNorte(),
                pessoa.getAtividadePrincipal(),

                pessoa.getRegimeDeBens(),
                pessoa.getDataCasamento(),

                Optional.ofNullable(pessoa.getIsRecebePronaf()).orElse(false),
                Optional.ofNullable(pessoa.getIsRecebeAjudoProgramaGoverno()).orElse(false),
                qtdPronaf,
                totalPronafs,

                pessoa.getRacaCor(),
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
        pessoa.setIsRecebePronaf(Boolean.TRUE.equals(isRecebePronaf));
        pessoa.setIsRecebeAjudoProgramaGoverno(Boolean.TRUE.equals(isRecebeAjudoProgramaGoverno));
        pessoa.setQtdPronaf(qtdPronaf != null ? qtdPronaf : 0);
        pessoa.setValorTotalPronafs(valorTotalPronafs != null ? valorTotalPronafs : BigDecimal.ZERO);
        pessoa.setRacaCor(racaCor);
        // **Relacionamentos:**
        // Aqui você pode popular programasDoGoverno via Service/Repository no Controller se precisar.
        return pessoa;
    }
}

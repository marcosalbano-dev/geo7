package org.geo7.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.geo7.model.entity.ConjugePessoa;
import org.geo7.model.entity.Municipio;
import org.geo7.model.entity.Pessoa;

import java.util.Date;
import java.util.Optional;

public record ConjugePessoaDTO(

        Long id,
        String nome,
        String telefone,
        String email,
        String nomePai,
        String nomeMae,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date dataNascimento,
        String sexoPessoa,

        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cep,

        String codigoPaisResidencia,
        String codigoPaisOrigem,

        String tipoDocumentoIdentificacao,
        String descricaoOutroDocumentoIdentificacao,
        String numeroDocumentoIdentificacao,
        String orgaoEmissor,
        String ufOrgaoEmissor,
        String tipoNacionalidade,
        String cpf,
        String racaCor,
        Boolean conjugeOk,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        Date validadeRne,

        // Relacionamentos
        Long pessoaId,
        Long municipioResidenciaId,
        Long municipioNaturalidadeId,

        // Derivados
        String uf,
        String ufNaturalidade

) {
    public static ConjugePessoaDTO fromEntity(ConjugePessoa c) {
        return new ConjugePessoaDTO(
                c.getId(),
                c.getNome(),
                c.getTelefone(),
                c.getEmail(),
                c.getNomePai(),
                c.getNomeMae(),
                c.getDataNascimento(),
                c.getSexoPessoa(),

                c.getLogradouro(),
                c.getNumero(),
                c.getComplemento(),
                c.getBairro(),
                c.getCep(),

                Optional.ofNullable(c.getCodigoPaisResidencia()).orElse("931"),
                Optional.ofNullable(c.getCodigoPaisOrigem()).orElse("931"),

                c.getTipoDocumentoIdentificacao(),
                c.getDescricaoOutroDocumentoIdentificacao(),
                c.getNumeroDocumentoIdentificacao(),
                c.getOrgaoEmissor(),
                Optional.ofNullable(c.getUfOrgaoEmissor()).orElse("CE"),
                c.getTipoNacionalidade(),
                c.getCpf(),
                c.getRacaCor(),
                Optional.ofNullable(c.getConjugeOk()).orElse(false),
                c.getValidadeRne(),

                null != c.getPessoa() ? c.getPessoa().getId() : null,
                null != c.getMunicipioResidencia() ? c.getMunicipioResidencia().getId() : null,
                null != c.getMunicipioNaturalidade() ? c.getMunicipioNaturalidade().getId() : null,

                c.getUf(),
                c.getUfNaturalidade()
        );
    }

    public ConjugePessoa toEntity(Pessoa pessoa, Municipio municipioResidencia, Municipio municipioNaturalidade) {
        ConjugePessoa c = new ConjugePessoa();
        c.setId(this.id);
        c.setNome(this.nome);
        c.setTelefone(this.telefone);
        c.setEmail(this.email);
        c.setNomePai(this.nomePai);
        c.setNomeMae(this.nomeMae);
        c.setDataNascimento(this.dataNascimento);
        c.setSexoPessoa(this.sexoPessoa);

        c.setLogradouro(this.logradouro);
        c.setNumero(this.numero);
        c.setComplemento(this.complemento);
        c.setBairro(this.bairro);
        c.setCep(this.cep);

        c.setCodigoPaisResidencia(null != codigoPaisResidencia ? this.codigoPaisResidencia : "931");
        c.setCodigoPaisOrigem(null != codigoPaisOrigem ? this.codigoPaisOrigem : "931");

        c.setTipoDocumentoIdentificacao(this.tipoDocumentoIdentificacao);
        c.setDescricaoOutroDocumentoIdentificacao(this.descricaoOutroDocumentoIdentificacao);
        c.setNumeroDocumentoIdentificacao(this.numeroDocumentoIdentificacao);
        c.setOrgaoEmissor(this.orgaoEmissor);
        c.setUfOrgaoEmissor(null != ufOrgaoEmissor ? this.ufOrgaoEmissor : "CE");
        c.setTipoNacionalidade(this.tipoNacionalidade);
        c.setCpf(this.cpf);
        c.setRacaCor(this.racaCor);
        c.setConjugeOk(null != conjugeOk ? this.conjugeOk : false);
        c.setValidadeRne(this.validadeRne);

        // Relacionamentos
        if (null != pessoa) c.setPessoa(pessoa);
        if (null != municipioResidencia) c.setMunicipioResidencia(municipioResidencia);
        if (null != municipioNaturalidade) c.setMunicipioNaturalidade(municipioNaturalidade);

        return c;
    }
}


package org.geo7.dto;

import org.geo7.model.entity.DocumentoPessoa;
import org.geo7.model.entity.Municipio;
import org.geo7.model.entity.Pessoa;

public record DocumentoPessoaDTO(
        Long id,
        Long pessoaId,
        String tipoDocumentoIdentificacao,
        String numeroDocumentoIdentificacao,
        String orgaoEmissor,
        String ufOrgaoEmissor,
        String tipoNacionalidade,
        String cpf,
        String codigoPaisOrigem,
        String estadoCivil,
        String tipoPessoa,
        String cnpj,
        String naturezaJuridica,
        Double capitalNacional,
        Double capitalEstrangeiro,
        String registroJuntaComercial,
        String nomeFantasia,
        String codigoPaisSede,
        String ufPaisSede,
        String tipoDocumentoRepresentanteLegal,
        String numeroDocumentoRepresentanteLegal,
        String codigoPaisResidencia,
        String tipoDePoder,
        String tipoDeGoverno,
        String percentCapitalNacional,
        String percentCapitalEstrangeiro,
        String pcePais,
        String pcePercentCapital,
        String obsevacoesQuadro7,
        Long naturalidadeId
) {
    public static DocumentoPessoaDTO fromEntity(DocumentoPessoa d) {
        return new DocumentoPessoaDTO(
                d.getId(),
                null != d.getPessoa() ? d.getPessoa().getId() : null,
                d.getTipoDocumentoIdentificacao(),
                d.getNumeroDocumentoIdentificacao(),
                d.getOrgaoEmissor(),
                d.getUfOrgaoEmissor(),
                d.getTipoNacionalidade(),
                d.getCpf(),
                d.getCodigoPaisOrigem(),
                d.getEstadoCivil(),
                d.getTipoPessoa(),
                d.getCnpj(),
                d.getNaturezaJuridica(),
                d.getCapitalNacional(),
                d.getCapitalEstrangeiro(),
                d.getRegistroJuntaComercial(),
                d.getNomeFantasia(),
                d.getCodigoPaisSede(),
                d.getUfPaisSede(),
                d.getTipoDocumentoRepresentanteLegal(),
                d.getNumeroDocumentoRepresentanteLegal(),
                d.getCodigoPaisResidencia(),
                d.getTipoDePoder(),
                d.getTipoDeGoverno(),
                d.getPercentCapitalNacional(),
                d.getPercentCapitalEstrangeiro(),
                d.getPcePais(),
                d.getPcePercentCapital(),
                d.getObsevacoesQuadro7(),
                null != d.getNaturalidade() ? d.getNaturalidade().getId() : null
        );
    }

    public DocumentoPessoa toEntity(Pessoa pessoa, Municipio naturalidade) {
        DocumentoPessoa d = new DocumentoPessoa();
        d.setId(id());
        d.setPessoa(pessoa); // deve ser a entidade já buscada no service!
        d.setTipoDocumentoIdentificacao(tipoDocumentoIdentificacao());
        d.setNumeroDocumentoIdentificacao(numeroDocumentoIdentificacao());
        d.setOrgaoEmissor(orgaoEmissor());
        d.setUfOrgaoEmissor(ufOrgaoEmissor());
        d.setTipoNacionalidade(tipoNacionalidade());
        d.setCpf(cpf());
        d.setCodigoPaisOrigem(codigoPaisOrigem());
        d.setEstadoCivil(estadoCivil());
        d.setTipoPessoa(tipoPessoa());
        d.setCnpj(cnpj());
        d.setNaturezaJuridica(naturezaJuridica());
        d.setCapitalNacional(capitalNacional());
        d.setCapitalEstrangeiro(capitalEstrangeiro());
        d.setRegistroJuntaComercial(registroJuntaComercial());
        d.setNomeFantasia(nomeFantasia());
        d.setCodigoPaisSede(codigoPaisSede());
        d.setUfPaisSede(ufPaisSede());
        d.setTipoDocumentoRepresentanteLegal(tipoDocumentoRepresentanteLegal());
        d.setNumeroDocumentoRepresentanteLegal(numeroDocumentoRepresentanteLegal());
        d.setCodigoPaisResidencia(codigoPaisResidencia());
        d.setTipoDePoder(tipoDePoder());
        d.setTipoDeGoverno(tipoDeGoverno());
        d.setPercentCapitalNacional(percentCapitalNacional());
        d.setPercentCapitalEstrangeiro(percentCapitalEstrangeiro());
        d.setPcePais(pcePais());
        d.setPcePercentCapital(pcePercentCapital());
        d.setObsevacoesQuadro7(obsevacoesQuadro7());
        d.setNaturalidade(naturalidade); // também deve ser buscada no service!
        return d;
    }
}

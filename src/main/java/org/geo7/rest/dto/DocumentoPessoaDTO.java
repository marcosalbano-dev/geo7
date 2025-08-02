package org.geo7.rest.dto;

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
                d.getPessoa() != null ? d.getPessoa().getId() : null,
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
                d.getNaturalidade() != null ? d.getNaturalidade().getId() : null
        );
    }

    public DocumentoPessoa toEntity(Pessoa pessoa, Municipio naturalidade) {
        DocumentoPessoa d = new DocumentoPessoa();
        d.setId(this.id());
        d.setPessoa(pessoa); // deve ser a entidade já buscada no service!
        d.setTipoDocumentoIdentificacao(this.tipoDocumentoIdentificacao());
        d.setNumeroDocumentoIdentificacao(this.numeroDocumentoIdentificacao());
        d.setOrgaoEmissor(this.orgaoEmissor());
        d.setUfOrgaoEmissor(this.ufOrgaoEmissor());
        d.setTipoNacionalidade(this.tipoNacionalidade());
        d.setCpf(this.cpf());
        d.setCodigoPaisOrigem(this.codigoPaisOrigem());
        d.setEstadoCivil(this.estadoCivil());
        d.setTipoPessoa(this.tipoPessoa());
        d.setCnpj(this.cnpj());
        d.setNaturezaJuridica(this.naturezaJuridica());
        d.setCapitalNacional(this.capitalNacional());
        d.setCapitalEstrangeiro(this.capitalEstrangeiro());
        d.setRegistroJuntaComercial(this.registroJuntaComercial());
        d.setNomeFantasia(this.nomeFantasia());
        d.setCodigoPaisSede(this.codigoPaisSede());
        d.setUfPaisSede(this.ufPaisSede());
        d.setTipoDocumentoRepresentanteLegal(this.tipoDocumentoRepresentanteLegal());
        d.setNumeroDocumentoRepresentanteLegal(this.numeroDocumentoRepresentanteLegal());
        d.setCodigoPaisResidencia(this.codigoPaisResidencia());
        d.setTipoDePoder(this.tipoDePoder());
        d.setTipoDeGoverno(this.tipoDeGoverno());
        d.setPercentCapitalNacional(this.percentCapitalNacional());
        d.setPercentCapitalEstrangeiro(this.percentCapitalEstrangeiro());
        d.setPcePais(this.pcePais());
        d.setPcePercentCapital(this.pcePercentCapital());
        d.setObsevacoesQuadro7(this.obsevacoesQuadro7());
        d.setNaturalidade(naturalidade); // também deve ser buscada no service!
        return d;
    }
}

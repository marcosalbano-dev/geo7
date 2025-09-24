package org.geo7.dto;

public class LoteFiltroDTO {

    private String cpf;
    private String proprietario;
    private Long municipioId;
    private String numero;
    private String denominacaoImovel;

    public String getCpf() {
        return this.cpf;
    }

    public void setCpf(final String cpf) {
        this.cpf = cpf;
    }

    public String getProprietario() {
        return this.proprietario;
    }

    public void setProprietario(final String proprietario) {
        this.proprietario = proprietario;
    }

    public Long getMunicipioId() {
        return this.municipioId;
    }

    public void setMunicipioId(final Long municipioId) {
        this.municipioId = municipioId;
    }

    public String getNumero() {
        return this.numero;
    }

    public void setNumero(final String numero) {
        this.numero = numero;
    }

    public String getDenominacaoImovel() {
        return this.denominacaoImovel;
    }

    public void setDenominacaoImovel(final String denominacaoImovel) {
        this.denominacaoImovel = denominacaoImovel;
    }
}

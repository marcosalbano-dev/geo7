package org.geo7.dto;

public class LoteFiltroDTO {

    private String cpf;
    private String proprietario;
    private Long municipioId;
    private String numero;
    private String denominacaoImovel;

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getProprietario() {
        return proprietario;
    }

    public void setProprietario(String proprietario) {
        this.proprietario = proprietario;
    }

    public Long getMunicipioId() {
        return municipioId;
    }

    public void setMunicipioId(Long municipioId) {
        this.municipioId = municipioId;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getDenominacaoImovel() {
        return denominacaoImovel;
    }

    public void setDenominacaoImovel(String denominacaoImovel) {
        this.denominacaoImovel = denominacaoImovel;
    }
}

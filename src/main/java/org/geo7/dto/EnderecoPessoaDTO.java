package org.geo7.dto;

import org.geo7.model.entity.EnderecoPessoa;

public record EnderecoPessoaDTO(
        Long id,
        String logradouro,
        String complemento,
        String numero,
        String bairro,
        String cep,
        String codigoPaisResidencia,
        Long municipioId,
        String municipioNome,
        String uf,
        Long pessoaId
) {
    // Converte de Entidade para DTO
    public static EnderecoPessoaDTO fromEntity(EnderecoPessoa e) {
        return new EnderecoPessoaDTO(
                e.getId(),
                e.getLogradouro(),
                e.getComplemento(),
                e.getNumero(),
                e.getBairro(),
                e.getCep(),
                e.getCodigoPaisResidencia(),
                e.getMunicipio() != null ? e.getMunicipio().getId() : null,
                e.getMunicipio() != null ? e.getMunicipio().getNome() : null,
                e.getMunicipio() != null ? e.getMunicipio().getUf() : null,
                e.getPessoa() != null ? e.getPessoa().getId() : null
        );
    }

    // Converte de DTO para Entidade (útil no service!)
    public EnderecoPessoa toEntity() {
        EnderecoPessoa endereco = new EnderecoPessoa();
        endereco.setId(this.id());
        endereco.setLogradouro(this.logradouro());
        endereco.setComplemento(this.complemento());
        endereco.setNumero(this.numero());
        endereco.setBairro(this.bairro());
        endereco.setCep(this.cep());
        endereco.setCodigoPaisResidencia(this.codigoPaisResidencia());


        // Observação: municipio e pessoa devem ser setados no service, pois precisa buscar entidades completas!
        return endereco;
    }
}

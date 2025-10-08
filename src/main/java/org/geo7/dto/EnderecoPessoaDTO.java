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
                null != e.getMunicipio() ? e.getMunicipio().getId() : null,
                null != e.getMunicipio() ? e.getMunicipio().getNome() : null,
                null != e.getMunicipio() ? e.getMunicipio().getUf() : null,
                null != e.getPessoa() ? e.getPessoa().getId() : null
        );
    }

    // Converte de DTO para Entidade (útil no service!)
    public EnderecoPessoa toEntity() {
        EnderecoPessoa endereco = new EnderecoPessoa();
        endereco.setId(id());
        endereco.setLogradouro(logradouro());
        endereco.setComplemento(complemento());
        endereco.setNumero(numero());
        endereco.setBairro(bairro());
        endereco.setCep(cep());
        endereco.setCodigoPaisResidencia(codigoPaisResidencia());


        // Observação: municipio e pessoa devem ser setados no service, pois precisa buscar entidades completas!
        return endereco;
    }
}

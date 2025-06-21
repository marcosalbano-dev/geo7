package org.geo7.rest.dto;

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
        String uf
) {
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
                e.getMunicipio() != null ? e.getMunicipio().getUf() : null
        );
    }
}

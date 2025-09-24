package org.geo7.dto;

public record EditarDetentorResponseDTO(
        PessoaDTO pessoa,
        PessoaLoteDTO pessoaLote,
        EnderecoPessoaDTO endereco,
        DocumentoPessoaDTO documento
) {}


package org.geo7.rest.dto;

public record AtualizaDetentorRequestDTO(
        PessoaDTO pessoa,
        PessoaLoteDTO pessoaLote,
        EnderecoPessoaDTO endereco,
        DocumentoPessoaDTO documento
) {}



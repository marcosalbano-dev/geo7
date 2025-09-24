package org.geo7.dto;

public record AtualizaDetentorRequestDTO(
        PessoaDTO pessoa,
        PessoaLoteDTO pessoaLote,
        EnderecoPessoaDTO endereco,
        DocumentoPessoaDTO documento
) {}



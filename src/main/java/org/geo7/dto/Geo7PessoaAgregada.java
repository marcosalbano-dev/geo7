package org.geo7.dto;

public record Geo7PessoaAgregada(
        PessoaDTO pessoa,
        PessoaLoteDTO pessoaLote,
        EnderecoPessoaDTO endereco,
        DocumentoPessoaDTO documento
) {}

package org.geo7.rest.dto;

import lombok.Data;

@Data
public class AtualizaDetentorRequestDTO {
    private PessoaDTO pessoa;
    private PessoaLoteDTO pessoaLote;
    private EnderecoPessoaDTO endereco;
    private DocumentoPessoaDTO documento;
}


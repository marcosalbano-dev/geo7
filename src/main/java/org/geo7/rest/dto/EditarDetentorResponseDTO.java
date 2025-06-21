package org.geo7.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EditarDetentorResponseDTO {
    private PessoaDTO pessoa;
    private PessoaLoteDTO pessoaLote;
    private EnderecoPessoaDTO endereco;
    private DocumentoPessoaDTO documento;
}


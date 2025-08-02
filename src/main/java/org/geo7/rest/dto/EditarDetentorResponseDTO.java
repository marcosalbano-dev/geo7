package org.geo7.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

public record EditarDetentorResponseDTO(
        PessoaDTO pessoa,
        PessoaLoteDTO pessoaLote,
        EnderecoPessoaDTO endereco,
        DocumentoPessoaDTO documento
) {}


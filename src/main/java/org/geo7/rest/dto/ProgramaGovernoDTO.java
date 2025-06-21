package org.geo7.rest.dto;

import org.geo7.model.entity.ProgramaGoverno;

public record ProgramaGovernoDTO(
        Long id,
        String nome
) {
    public static ProgramaGovernoDTO fromEntity(ProgramaGoverno programa) {
        return new ProgramaGovernoDTO(
                programa.getId(),
                programa.getNome()
        );
    }

    public ProgramaGoverno toEntity() {
        ProgramaGoverno programa = new ProgramaGoverno();
        programa.setId(id);
        programa.setNome(nome);
        return programa;
    }
}


package org.geo7.rest.dto;

import org.geo7.model.entity.Pessoa;
import org.geo7.model.entity.Pronaf;

public record PronafDTO(
        Long id,
        String tipo,
        String faixaI,
        String faixaII,
        String faixaIII,
        Long pessoaId
) {
    public static PronafDTO fromEntity(Pronaf pronaf) {
        return new PronafDTO(
                pronaf.getId(),
                pronaf.getTipo(),
                pronaf.getFaixaI(),
                pronaf.getFaixaII(),
                pronaf.getFaixaIII(),
                pronaf.getPessoa() != null ? pronaf.getPessoa().getId() : null
        );
    }

    public Pronaf toEntity(Pessoa pessoa) {
        Pronaf pronaf = new Pronaf();
        pronaf.setId(id);
        pronaf.setTipo(tipo);
        pronaf.setFaixaI(faixaI);
        pronaf.setFaixaII(faixaII);
        pronaf.setFaixaIII(faixaIII);
        pronaf.setPessoa(pessoa);
        return pronaf;
    }
}


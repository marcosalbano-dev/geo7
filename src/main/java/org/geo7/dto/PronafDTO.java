package org.geo7.dto;

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
                null != pronaf.getPessoa() ? pronaf.getPessoa().getId() : null
        );
    }

    public Pronaf toEntity(Pessoa pessoa) {
        Pronaf pronaf = new Pronaf();
        pronaf.setId(this.id);
        pronaf.setTipo(this.tipo);
        pronaf.setFaixaI(this.faixaI);
        pronaf.setFaixaII(this.faixaII);
        pronaf.setFaixaIII(this.faixaIII);
        pronaf.setPessoa(pessoa);
        return pronaf;
    }
}


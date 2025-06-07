package org.geo7.rest.dto;

import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.SituacaoJuridica;

import java.math.BigDecimal;
import java.util.Date;

public record FormaObtencaoDTO(
        Long id,
        String descricaoFormaDeObtencao,
        String oficio,
        String matricula,
        String livro,
        String nomeCartorio,
        String dataRegistro,
        String numeroRegistro,
        BigDecimal areaRegistrada,
        BigDecimal areaMedida,
        String municipioCartorio,
        Integer numeroHerdeiros,
        Date dataPosse,
        Long loteId,
        Long situacaoJuridicaId
) {
    public static FormaObtencaoDTO fromEntity(FormaObtencao entity) {
        return new FormaObtencaoDTO(
                entity.getId(),
                entity.getDescricaoFormaDeObtencao(),
                entity.getOficio(),
                entity.getMatricula(),
                entity.getLivro(),
                entity.getNomeCartorio(),
                entity.getDataRegistro(),
                entity.getNumeroRegistro(),
                entity.getAreaRegistrada(),
                entity.getAreaMedida(),
                entity.getMunicipioCartorio(),
                entity.getNumeroHerdeiros(),
                entity.getDataPosse(),
                entity.getLote() != null ? entity.getLote().getId() : null,
                entity.getSituacaoJuridica() != null ? entity.getSituacaoJuridica().getId() : null
        );
    }

    public FormaObtencao toEntity(Lote lote, SituacaoJuridica situacaoJuridica) {
        return FormaObtencao.builder()
                .id(id)
                .descricaoFormaDeObtencao(descricaoFormaDeObtencao)
                .oficio(oficio)
                .matricula(matricula)
                .livro(livro)
                .nomeCartorio(nomeCartorio)
                .dataRegistro(dataRegistro)
                .numeroRegistro(numeroRegistro)
                .areaRegistrada(areaRegistrada != null ? areaRegistrada : BigDecimal.ZERO)
                .areaMedida(areaMedida != null ? areaMedida : BigDecimal.ZERO)
                .municipioCartorio(municipioCartorio)
                .numeroHerdeiros(numeroHerdeiros)
                .dataPosse(dataPosse)
                .lote(lote)
                .situacaoJuridica(situacaoJuridica)
                .build();
    }
}

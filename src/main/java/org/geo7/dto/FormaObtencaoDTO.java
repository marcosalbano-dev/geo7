package org.geo7.dto;

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
                null != entity.getLote() ? entity.getLote().getId() : null,
                null != entity.getSituacaoJuridica() ? entity.getSituacaoJuridica().getId() : null
        );
    }

    public FormaObtencao toEntity(Lote lote, SituacaoJuridica situacaoJuridica) {
        return FormaObtencao.builder()
                .id(this.id)
                .descricaoFormaDeObtencao(this.descricaoFormaDeObtencao)
                .oficio(this.oficio)
                .matricula(this.matricula)
                .livro(this.livro)
                .nomeCartorio(this.nomeCartorio)
                .dataRegistro(this.dataRegistro)
                .numeroRegistro(this.numeroRegistro)
                .areaRegistrada(null != areaRegistrada ? this.areaRegistrada : BigDecimal.ZERO)
                .areaMedida(null != areaMedida ? this.areaMedida : BigDecimal.ZERO)
                .municipioCartorio(this.municipioCartorio)
                .numeroHerdeiros(this.numeroHerdeiros)
                .dataPosse(this.dataPosse)
                .lote(lote)
                .situacaoJuridica(situacaoJuridica)
                .build();
    }
}

package org.geo7.rest.mapper;

import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.SituacaoJuridica;
import org.geo7.rest.dto.SituacaoJuridicaDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;

public class SituacaoJuridicaMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static SituacaoJuridicaDTO toDTO(SituacaoJuridica entity) {
        FormaObtencao forma = entity.getFormaObtencao().stream().findFirst().orElse(null);
        return new SituacaoJuridicaDTO(
                entity.getId(),
                forma != null ? forma.getLote().getId() : null,
                entity.getNome(),
                forma != null ? forma.getDescricaoFormaDeObtencao() : null,
                forma != null ? forma.getDataPosse() : null,
                forma != null ? forma.getAreaMedida() : null,
                forma != null ? forma.getLivro() : null,
                forma != null ? forma.getAreaRegistrada() : null,
                forma != null ? forma.getNomeCartorio() : null,
                forma != null ? forma.getMunicipioCartorio() : null,
                forma != null ? forma.getDataRegistro() : null,
                forma != null ? forma.getOficio() : null,
                forma != null ? forma.getMatricula() : null,
                forma != null ? forma.getNumeroRegistro() : null
        );
    }

    public static SituacaoJuridica toEntity(SituacaoJuridicaDTO dto, Lote lote) {
        SituacaoJuridica sj = new SituacaoJuridica();
        sj.setNome(dto.situacaoSelecionada());

        FormaObtencao forma = FormaObtencao.builder()
                .descricaoFormaDeObtencao(dto.formaObtencaoSelecionada())
                .dataPosse(dto.dataPosse())
                .areaMedida(dto.areaPosse() != null ? dto.areaPosse() : BigDecimal.ZERO)
                .livro(dto.livro())
                .areaRegistrada(dto.areaRegistrada())
                .nomeCartorio(dto.nomeCartorio())
                .municipioCartorio(dto.municipioCartorio())
                .dataRegistro(dto.dataRegistro())
                .oficio(dto.oficio())
                .matricula(dto.matricula())
                .numeroRegistro(dto.numeroRegistro())
                .lote(lote)
                .situacaoJuridica(sj)
                .build();

        sj.getLotes().add(lote); // se necessário manter bidirecional
        lote.setSituacaoJuridica(sj); // se mapeamento estiver presente em Lote
        sj.getFormaObtencao().add(forma); // supondo que seja lista

        return sj;
    }

    private static Date parseData(String dataStr) {
        if (dataStr == null || dataStr.isBlank()) return null;
        LocalDate localDate = LocalDate.parse(dataStr, FORMATTER);
        return java.sql.Date.valueOf(localDate);
    }

    private static BigDecimal parseBigDecimal(String valor) {
        if (valor == null || valor.isBlank()) return BigDecimal.ZERO;
        return new BigDecimal(valor.replace(",", "."));
    }
}

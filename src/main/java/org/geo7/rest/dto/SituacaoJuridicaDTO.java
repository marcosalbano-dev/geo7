package org.geo7.rest.dto;

import org.geo7.model.entity.SituacaoJuridica;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;

public record SituacaoJuridicaDTO(
        Long id,
        // ID da situação jurídica (caso seja edição)
        Long loteId,                        // Referência ao Lote
        String situacaoSelecionada,         // Tipo de situação
        String formaObtencaoSelecionada,    // Ex: "07 - Compra e Venda de Particular"
        Date dataPosse,
        BigDecimal areaPosse,

        // Campos opcionais (caso não seja simples ocupação ou justo título)
        String livro,
        BigDecimal areaRegistrada,
        String nomeCartorio,
        String municipioCartorio,
        String dataRegistro,  // se precisar ser convertido para Date, ajustar no mapper
        String oficio,
        String matricula,
        String numeroRegistro
) {}



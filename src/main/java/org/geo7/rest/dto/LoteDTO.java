package org.geo7.rest.dto;

import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.Municipio;
import org.geo7.model.entity.SituacaoJuridica;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

public record LoteDTO(
        Long id,
        String proprietario,
        BigDecimal area,
        String denominacaoImovel,
        String numero,
        Date dhc,
        Date dhm,
        Double perimetro,
        String sncr,
        String cpf,
        Long municipioId,
        String municipioNome,
        Set<FormaObtencaoDTO> formaObtencao,
        Long situacaoJuridicaId,
        String dataTerminoPeriodoDeUso
) {
    public static LoteDTO fromEntity(Lote lote) {
        return new LoteDTO(
                lote.getId(),
                lote.getProprietario(),
                lote.getArea(),
                lote.getDenominacaoImovel(),
                lote.getNumero(),
                lote.getDhc(),
                lote.getDhm(),
                lote.getPerimetro(),
                lote.getSncr(),
                lote.getCpf(),
                lote.getMunicipio() != null ? lote.getMunicipio().getId() : null,
                lote.getMunicipio() != null ? lote.getMunicipio().getNome() : null,
                lote.getFormaObtencao().stream()
                        .map(FormaObtencaoDTO::fromEntity)
                        .collect(Collectors.toSet()),
                lote.getSituacaoJuridica() != null ? lote.getSituacaoJuridica().getId() : null,
                lote.getDataTerminoPeriodoDeUso()
        );
    }

    public Lote toEntity(Municipio municipio, SituacaoJuridica situacaoJuridica, Set<FormaObtencao> formas) {
        Lote lote = new Lote();
        lote.setId(id);
        lote.setProprietario(proprietario);
        lote.setArea(area);
        lote.setDenominacaoImovel(denominacaoImovel);
        lote.setNumero(numero);
        lote.setDhc(dhc != null ? dhc : new Date());
        lote.setDhm(dhm != null ? dhm : new Date());
        lote.setPerimetro(perimetro);
        lote.setSncr(sncr);
        lote.setCpf(cpf);
        lote.setMunicipio(municipio);
        lote.setSituacaoJuridica(situacaoJuridica);
        lote.setDataTerminoPeriodoDeUso(dataTerminoPeriodoDeUso);

        formas.forEach(f -> f.setLote(lote)); // vincula o lote a cada forma
        lote.setFormaObtencao(formas);

        return lote;
    }
}


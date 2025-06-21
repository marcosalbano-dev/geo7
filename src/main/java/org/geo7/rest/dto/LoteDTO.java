package org.geo7.rest.dto;

import org.geo7.model.entity.*;

import java.math.BigDecimal;
import java.util.Date;

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
        String formaObtencaoSelecionada, // substitui Set<FormaObtencaoDTO>
        Long situacaoJuridicaId,
        String dataTerminoPeriodoDeUso,
        Long distritoId
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
                lote.getFormaObtencao() != null && !lote.getFormaObtencao().isEmpty()
                        ? lote.getFormaObtencao().iterator().next().getDescricaoFormaDeObtencao() : null,
                lote.getSituacaoJuridica() != null ? lote.getSituacaoJuridica().getId() : null,
                lote.getDataTerminoPeriodoDeUso(),
                lote.getDistrito() != null ? lote.getDistrito().getId() : null
        );
    }

    public Lote toEntity(Municipio municipio, SituacaoJuridica situacaoJuridica, Distrito distrito) {
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
        lote.setDistrito(distrito);
        lote.setDataTerminoPeriodoDeUso(dataTerminoPeriodoDeUso);
        return lote;
    }
}

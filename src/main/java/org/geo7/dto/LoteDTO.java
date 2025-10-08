package org.geo7.dto;

import org.geo7.model.entity.Distrito;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.Municipio;
import org.geo7.model.entity.SituacaoJuridica;

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
        Long distritoId,
        String distritoNome,
        String situacaoJuridicaNome
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
                null != lote.getMunicipio() ? lote.getMunicipio().getId() : null,
                null != lote.getMunicipio() ? lote.getMunicipio().getNome() : null,
                null != lote.getFormaObtencao() && !lote.getFormaObtencao().isEmpty()
                        ? lote.getFormaObtencao().iterator().next().getDescricaoFormaDeObtencao() : null,
                null != lote.getSituacaoJuridica() ? lote.getSituacaoJuridica().getId() : null,
                lote.getDataTerminoPeriodoDeUso(),
                null != lote.getDistrito() ? lote.getDistrito().getId() : null,
                null != lote.getDistrito() ? lote.getDistrito().getNomeDistrito() : null,
                null != lote.getSituacaoJuridica() ? lote.getSituacaoJuridica().getNome() : null
        );
    }

    public Lote toEntity(Municipio municipio, SituacaoJuridica situacaoJuridica, Distrito distrito) {
        Lote lote = new Lote();
        lote.setId(this.id);
        lote.setProprietario(this.proprietario);
        lote.setArea(this.area);
        lote.setDenominacaoImovel(this.denominacaoImovel);
        lote.setNumero(this.numero);
        lote.setDhc(null != dhc ? this.dhc : new Date());
        lote.setDhm(null != dhm ? this.dhm : new Date());
        lote.setPerimetro(this.perimetro);
        lote.setSncr(this.sncr);
        lote.setCpf(this.cpf);
        lote.setMunicipio(municipio);
        lote.setSituacaoJuridica(situacaoJuridica);
        lote.setDistrito(distrito);
        lote.setDataTerminoPeriodoDeUso(this.dataTerminoPeriodoDeUso);
        return lote;
    }
}

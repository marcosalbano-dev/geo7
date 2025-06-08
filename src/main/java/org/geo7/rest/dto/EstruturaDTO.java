package org.geo7.rest.dto;

import jakarta.validation.constraints.NotNull;
import org.geo7.model.entity.Estrutura;
import org.geo7.model.entity.Lote;

import java.math.BigDecimal;
import java.util.Date;

public record EstruturaDTO(
        Long id,
        @NotNull(message = "O ID do lote é obrigatório")
        Long loteId,
        Boolean ativo,
        Date dhc,
        Date dhm,
        Integer familiasResidentes,
        Integer pessoasResidentes,
        Integer trabalhadoresComCarteira,
        Integer trabalhadoresSemCarteira,
        Integer maoDeObraFamiliar,
        BigDecimal valorTotal,
        BigDecimal valorDasBenfeitorias,
        BigDecimal valorOutrasAtividades,
        BigDecimal valorTerraNua,
        BigDecimal areaIrrigada,
        String litigio,
        Boolean entregouMemorialPlanilha,
        String destinacaoDoImovel,
        String pontoDeReferencia,
        Integer numeroHerdeiros,
        Double porcentagemDetencao,
        String obsLitigio,
        Boolean isFonteAguaExterna,
        Boolean isPossuiElergiaEletrica,
        Boolean isPossuiEnergiaAlternativa,
        String tipoEnergiaEletrica,
        Boolean isIrrigacao,
        Boolean isAcude,
        Boolean isAcudePerene,
        String usoDaguaAcude,
        Boolean isLagoa,
        Boolean isLagoaPerene,
        String usoDaguaLagoa,
        Boolean isPoco,
        Boolean isPocoPerene,
        String usoDaguaPoco,
        Boolean isRioOuRiacho,
        Boolean isRioOuRiachoPerene,
        String usoDaguaRioOuRiacho,
        Boolean isOlhoDagua,
        Boolean isOlhoDaguaPerene,
        String usoDaguaOlhoDagua,
        Boolean isRedeDeAbastecimento
) {
    public static EstruturaDTO fromEntity(Estrutura estrutura) {
        return new EstruturaDTO(
                estrutura.getId(),
                estrutura.getLote() != null ? estrutura.getLote().getId() : null,
                estrutura.isAtivo(),
                estrutura.getDhc(),
                estrutura.getDhm(),
                estrutura.getFamiliasResidentes(),
                estrutura.getPessoasResidentes(),
                estrutura.getTrabalhadoresComCarteira(),
                estrutura.getTrabalhadoresSemCarteira(),
                estrutura.getMaoDeObraFamiliar(),
                estrutura.getValorTotal(),
                estrutura.getValorDasBenfeitorias(),
                estrutura.getValorOutrasAtividades(),
                estrutura.getValorTerraNua(),
                estrutura.getAreaIrrigada(),
                estrutura.getLitigio(),
                estrutura.getEntregouMemorialPlanilha(),
                estrutura.getDestinacaoDoImovel(),
                estrutura.getPontoDeReferencia(),
                estrutura.getNumeroHerdeiros(),
                estrutura.getPorcentagemDetencao(),
                estrutura.getObsLitigio(),
                estrutura.getIsFonteAguaExterna(),
                estrutura.getIsPossuiElergiaEletrica(),
                estrutura.getIsPossuiEnergiaAlternativa(),
                estrutura.getTipoEnergiaEletrica(),
                estrutura.getIsIrrigacao(),
                estrutura.getIsAcude(),
                estrutura.getIsAcudePerene(),
                estrutura.getUsoDaguaAcude(),
                estrutura.getIsLagoa(),
                estrutura.getIsLagoaPerene(),
                estrutura.getUsoDaguaLagoa(),
                estrutura.getIsPoco(),
                estrutura.getIsPocoPerene(),
                estrutura.getUsoDaguaPoco(),
                estrutura.getIsRioOuRiacho(),
                estrutura.getIsRioOuRiachoPerene(),
                estrutura.getUsoDaguaRioOuRiacho(),
                estrutura.getIsOlhoDagua(),
                estrutura.getIsOlhoDaguaPerene(),
                estrutura.getUsoDaguaOlhoDagua(),
                estrutura.getIsRedeDeAbastecimento()
        );
    }

    public Estrutura toEntity() {
        Estrutura estrutura = new Estrutura();
        estrutura.setId(this.id());
        estrutura.setAtivo(this.ativo() != null ? this.ativo() : true);
        estrutura.setDhc(this.dhc() != null ? this.dhc() : new Date());
        estrutura.setDhm(this.dhm() != null ? this.dhm() : new Date());
        estrutura.setFamiliasResidentes(this.familiasResidentes());
        estrutura.setPessoasResidentes(this.pessoasResidentes());
        estrutura.setTrabalhadoresComCarteira(this.trabalhadoresComCarteira());
        estrutura.setTrabalhadoresSemCarteira(this.trabalhadoresSemCarteira());
        estrutura.setMaoDeObraFamiliar(this.maoDeObraFamiliar());
        estrutura.setValorTotal(this.valorTotal() != null ? this.valorTotal() : BigDecimal.ZERO);
        estrutura.setValorDasBenfeitorias(this.valorDasBenfeitorias() != null ? this.valorDasBenfeitorias() : BigDecimal.ZERO);
        estrutura.setValorOutrasAtividades(this.valorOutrasAtividades() != null ? this.valorOutrasAtividades() : BigDecimal.ZERO);
        estrutura.setValorTerraNua(this.valorTerraNua() != null ? this.valorTerraNua() : BigDecimal.ZERO);
        estrutura.setAreaIrrigada(this.areaIrrigada() != null ? this.areaIrrigada() : BigDecimal.ZERO);
        estrutura.setLitigio(this.litigio());
        estrutura.setEntregouMemorialPlanilha(this.entregouMemorialPlanilha() != null ? this.entregouMemorialPlanilha() : false);
        estrutura.setDestinacaoDoImovel(this.destinacaoDoImovel());
        estrutura.setPontoDeReferencia(this.pontoDeReferencia());
        estrutura.setNumeroHerdeiros(this.numeroHerdeiros());
        estrutura.setPorcentagemDetencao(this.porcentagemDetencao() != null ? this.porcentagemDetencao() : 0.0);
        estrutura.setObsLitigio(this.obsLitigio());
        estrutura.setIsFonteAguaExterna(this.isFonteAguaExterna() != null ? this.isFonteAguaExterna() : false);
        estrutura.setIsPossuiElergiaEletrica(this.isPossuiElergiaEletrica() != null ? this.isPossuiElergiaEletrica() : false);
        estrutura.setIsPossuiEnergiaAlternativa(this.isPossuiEnergiaAlternativa() != null ? this.isPossuiEnergiaAlternativa() : false);
        estrutura.setTipoEnergiaEletrica(this.tipoEnergiaEletrica());
        estrutura.setIsIrrigacao(this.isIrrigacao() != null ? this.isIrrigacao() : false);
        estrutura.setIsAcude(this.isAcude() != null ? this.isAcude() : false);
        estrutura.setIsAcudePerene(this.isAcudePerene() != null ? this.isAcudePerene() : false);
        estrutura.setUsoDaguaAcude(this.usoDaguaAcude());
        estrutura.setIsLagoa(this.isLagoa() != null ? this.isLagoa() : false);
        estrutura.setIsLagoaPerene(this.isLagoaPerene() != null ? this.isLagoaPerene() : false);
        estrutura.setUsoDaguaLagoa(this.usoDaguaLagoa());
        estrutura.setIsPoco(this.isPoco() != null ? this.isPoco() : false);
        estrutura.setIsPocoPerene(this.isPocoPerene() != null ? this.isPocoPerene() : false);
        estrutura.setUsoDaguaPoco(this.usoDaguaPoco());
        estrutura.setIsRioOuRiacho(this.isRioOuRiacho() != null ? this.isRioOuRiacho() : false);
        estrutura.setIsRioOuRiachoPerene(this.isRioOuRiachoPerene() != null ? this.isRioOuRiachoPerene() : false);
        estrutura.setUsoDaguaRioOuRiacho(this.usoDaguaRioOuRiacho());
        estrutura.setIsOlhoDagua(this.isOlhoDagua() != null ? this.isOlhoDagua() : false);
        estrutura.setIsOlhoDaguaPerene(this.isOlhoDaguaPerene() != null ? this.isOlhoDaguaPerene() : false);
        estrutura.setUsoDaguaOlhoDagua(this.usoDaguaOlhoDagua());
        estrutura.setIsRedeDeAbastecimento(this.isRedeDeAbastecimento() != null ? this.isRedeDeAbastecimento() : false);

        return estrutura;
    }

    public Estrutura toEntity(Lote lote) {
        Estrutura estrutura = this.toEntity(); // Usa o método existente
        estrutura.setLote(lote); // Substitui o Lote dummy
        return estrutura;
    }
}

package org.geo7.rest.dto;

import org.geo7.model.entity.Estrutura;
import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.SituacaoJuridica;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

public record EstruturaDTO(
        Long id,
        Long loteId,
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
        String tipoEnergiaEletrica,
        String usoDaguaRioOuRiacho,
        String usoDaguaAcude,
        String usoDaguaOlhoDagua,
        String usoDaguaLagoa,
        String usoDaguaPoco,
        Boolean isRioOuRiacho,
        Boolean isRioOuRiachoPerene,
        Boolean isAcude,
        Boolean isAcudePerene,
        Boolean isOlhoDagua,
        Boolean isOlhoDaguaPerene,
        Boolean isLagoa,
        Boolean isLagoaPerene,
        Boolean isPoco,
        Boolean isPocoPerene,
        Boolean isFonteAguaExterna,
        Boolean isIrrigacao,
        Boolean isPossuiElergiaEletrica,
        Boolean isPossuiEnergiaAlternativa,
        Boolean isRedeDeAbastecimento,
        String formaObtencaoSelecionada,
        String oficio,
        String matricula,
        String livro,
        String nomeCartorio,
        String dataRegistro,
        String numeroRegistro,
        String areaRegistrada,
        String areaMedida,
        String municipioCartorio,
        Integer numeroHerdeirosForma,
        String dataPosse,
        String situacaoSelecionada
) {
    public static EstruturaDTO fromEntity(Estrutura estrutura) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        FormaObtencao forma = Optional.ofNullable(estrutura.getLote())
                .map(Lote::getFormaObtencao)
                .flatMap(set -> set.stream().findFirst())
                .orElse(null);

        SituacaoJuridica situacao = forma != null ? forma.getSituacaoJuridica() : null;

        // ⚠️ Ajuste de datas
        String dataRegistroFormatada = null;
        if (forma != null && forma.getDataRegistro() != null) {
            Object dr = forma.getDataRegistro();
            if (dr instanceof Date) {
                dataRegistroFormatada = sdf.format((Date) dr);
            } else {
                dataRegistroFormatada = dr.toString();
            }
        }

        String dataPosseFormatada = null;
        if (forma != null && forma.getDataPosse() != null) {
            Object dp = forma.getDataPosse();
            if (dp instanceof Date) {
                dataPosseFormatada = sdf.format((Date) dp);
            } else {
                dataPosseFormatada = dp.toString();
            }
        }

        return new EstruturaDTO(
                estrutura.getId(),
                estrutura.getLote() != null ? estrutura.getLote().getId() : null,
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
                estrutura.getTipoEnergiaEletrica(),
                estrutura.getUsoDaguaRioOuRiacho(),
                estrutura.getUsoDaguaAcude(),
                estrutura.getUsoDaguaOlhoDagua(),
                estrutura.getUsoDaguaLagoa(),
                estrutura.getUsoDaguaPoco(),
                estrutura.getIsRioOuRiacho(),
                estrutura.getIsRioOuRiachoPerene(),
                estrutura.getIsAcude(),
                estrutura.getIsAcudePerene(),
                estrutura.getIsOlhoDagua(),
                estrutura.getIsOlhoDaguaPerene(),
                estrutura.getIsLagoa(),
                estrutura.getIsLagoaPerene(),
                estrutura.getIsPoco(),
                estrutura.getIsPocoPerene(),
                estrutura.getIsFonteAguaExterna(),
                estrutura.getIsIrrigacao(),
                estrutura.getIsPossuiElergiaEletrica(),
                estrutura.getIsPossuiEnergiaAlternativa(),
                estrutura.getIsRedeDeAbastecimento(),
                forma != null ? forma.getDescricaoFormaDeObtencao() : null,
                forma != null ? forma.getOficio() : null,
                forma != null ? forma.getMatricula() : null,
                forma != null ? forma.getLivro() : null,
                forma != null ? forma.getNomeCartorio() : null,
                dataRegistroFormatada,  // Usando a variável segura
                forma != null ? forma.getNumeroRegistro() : null,
                forma != null && forma.getAreaRegistrada() != null ? forma.getAreaRegistrada().toPlainString() : null,
                forma != null && forma.getAreaMedida() != null ? forma.getAreaMedida().toPlainString() : null,
                forma != null ? forma.getMunicipioCartorio() : null,
                forma != null ? forma.getNumeroHerdeiros() : null,
                dataPosseFormatada, // Usando a variável segura
                situacao != null ? situacao.getNome() : null
        );
    }

    public Estrutura toEntity(Lote lote, SituacaoJuridica situacaoJuridica) {
        BigDecimal areaRegistradaValue = null;
        BigDecimal areaMedidaValue = null;
        Date dataPosseDate = null;
        Date dataRegistroDate = null;

        try {
            if (areaRegistrada != null && !areaRegistrada.isBlank()) {
                areaRegistradaValue = new BigDecimal(areaRegistrada);
            }
            if (areaMedida != null && !areaMedida.isBlank()) {
                areaMedidaValue = new BigDecimal(areaMedida);
            }
            if (dataPosse != null && !dataPosse.isBlank()) {
                dataPosseDate = new SimpleDateFormat("yyyy-MM-dd").parse(dataPosse);
            }
            if (dataRegistro != null && !dataRegistro.isBlank()) {
                dataRegistroDate = new SimpleDateFormat("yyyy-MM-dd").parse(dataRegistro);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao converter valores para BigDecimal ou Date", e);
        }

        FormaObtencao forma = FormaObtencao.builder()
                .descricaoFormaDeObtencao(formaObtencaoSelecionada)
                .oficio(oficio)
                .matricula(matricula)
                .livro(livro)
                .nomeCartorio(nomeCartorio)
                .dataRegistro(String.valueOf(dataRegistroDate))
                .numeroRegistro(numeroRegistro)
                .areaRegistrada(areaRegistradaValue)
                .areaMedida(areaMedidaValue)
                .municipioCartorio(municipioCartorio)
                .dataPosse(dataPosseDate)
                .numeroHerdeiros(numeroHerdeirosForma)
                .lote(lote)
                .situacaoJuridica(situacaoJuridica)
                .build();

        Estrutura estrutura = new Estrutura();
        estrutura.setId(id);
        estrutura.setLote(lote);
        estrutura.setDhc(dhc);
        estrutura.setDhm(dhm);
        estrutura.setFamiliasResidentes(familiasResidentes);
        estrutura.setPessoasResidentes(pessoasResidentes);
        estrutura.setTrabalhadoresComCarteira(trabalhadoresComCarteira);
        estrutura.setTrabalhadoresSemCarteira(trabalhadoresSemCarteira);
        estrutura.setMaoDeObraFamiliar(maoDeObraFamiliar);
        estrutura.setValorTotal(valorTotal);
        estrutura.setValorDasBenfeitorias(valorDasBenfeitorias);
        estrutura.setValorOutrasAtividades(valorOutrasAtividades);
        estrutura.setValorTerraNua(valorTerraNua);
        estrutura.setAreaIrrigada(areaIrrigada);
        estrutura.setLitigio(litigio);
        estrutura.setEntregouMemorialPlanilha(entregouMemorialPlanilha);
        estrutura.setDestinacaoDoImovel(destinacaoDoImovel);
        estrutura.setPontoDeReferencia(pontoDeReferencia);
        estrutura.setNumeroHerdeiros(numeroHerdeiros);
        estrutura.setObsLitigio(obsLitigio);
        estrutura.setPorcentagemDetencao(porcentagemDetencao);
        estrutura.setTipoEnergiaEletrica(tipoEnergiaEletrica);
        estrutura.setIsPossuiElergiaEletrica(isPossuiElergiaEletrica);
        estrutura.setIsPossuiEnergiaAlternativa(isPossuiEnergiaAlternativa);
        estrutura.setIsRedeDeAbastecimento(isRedeDeAbastecimento);
        estrutura.setIsIrrigacao(isIrrigacao);
        estrutura.setIsFonteAguaExterna(isFonteAguaExterna);
        estrutura.setIsRioOuRiacho(isRioOuRiacho);
        estrutura.setIsRioOuRiachoPerene(isRioOuRiachoPerene);
        estrutura.setIsAcude(isAcude);
        estrutura.setIsAcudePerene(isAcudePerene);
        estrutura.setIsOlhoDagua(isOlhoDagua);
        estrutura.setIsOlhoDaguaPerene(isOlhoDaguaPerene);
        estrutura.setIsLagoa(isLagoa);
        estrutura.setIsLagoaPerene(isLagoaPerene);
        estrutura.setIsPoco(isPoco);
        estrutura.setIsPocoPerene(isPocoPerene);
        estrutura.setUsoDaguaAcude(usoDaguaAcude);
        estrutura.setUsoDaguaLagoa(usoDaguaLagoa);
        estrutura.setUsoDaguaOlhoDagua(usoDaguaOlhoDagua);
        estrutura.setUsoDaguaPoco(usoDaguaPoco);
        estrutura.setUsoDaguaRioOuRiacho(usoDaguaRioOuRiacho);

        // associar forma obtencao se necessário externamente
        return estrutura;
    }
}

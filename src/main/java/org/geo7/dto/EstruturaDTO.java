package org.geo7.dto;

import org.geo7.model.entity.Estrutura;
import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.SituacaoJuridica;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public record EstruturaDTO(
        Long id,
        Long loteId,
        Long formaObtencaoId,
        String descricaoFormaDeObtencao,
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
        //String formaObtencaoSelecionada,
        //String situacaoSelecionada,
        Long situacaoJuridicaId,

        // CAMPOS DO LOTE
        String numero,
        String sncr,
        String denominacaoImovel,
        BigDecimal area,
        String municipioNome,
        Long municipioId,
        Long distritoId

) {
    public static EstruturaDTO fromEntity(Estrutura estrutura, FormaObtencao forma) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataRegistroFmt = null != forma ? EstruturaDTO.formatarData(forma.getDataRegistro()) : null;
        String dataPosseFmt    = null != forma ? EstruturaDTO.formatarData(forma.getDataPosse())    : null;

//        FormaObtencao formaObtencao = Optional.ofNullable(estrutura.getLote())
//                .map(Lote::getFormaObtencao)
//                .flatMap(set -> set.stream().findFirst())
//                .orElse(null);

        SituacaoJuridica situacao = null != forma ? forma.getSituacaoJuridica() : null;

//        String dataRegistroFormatada = (forma != null && forma.getDataRegistro() != null)
//                ? sdf.format(forma.getDataRegistro()) : null;
//
//        String dataPosseFormatada = (forma != null && forma.getDataPosse() != null)
//                ? sdf.format(forma.getDataPosse()) : null;

        return new EstruturaDTO(
                estrutura.getId(),
                null != estrutura.getLote() ? estrutura.getLote().getId() : null,
                /* forma de obtenção */
                null != forma ? forma.getId() : null,
                null != forma ? forma.getDescricaoFormaDeObtencao() : null,

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
                null != forma ? forma.getOficio() : null,
                null != forma ? forma.getMatricula() : null,
                null != forma ? forma.getLivro() : null,
                null != forma ? forma.getNomeCartorio() : null,
                dataRegistroFmt,
                null != forma ? forma.getNumeroRegistro() : null,
                null != forma && null != forma.getAreaRegistrada() ? forma.getAreaRegistrada().toPlainString() : null,
                null != forma && null != forma.getAreaMedida() ? forma.getAreaMedida().toPlainString() : null,
                null != forma ? forma.getMunicipioCartorio() : null,
                null != forma ? forma.getNumeroHerdeiros() : null,
                dataPosseFmt,

                null != situacao ? situacao.getId() : null,
                null != estrutura.getLote() ? estrutura.getLote().getNumero() : null,
                null != estrutura.getLote() ? estrutura.getLote().getSncr() : null,
                null != estrutura.getLote() ? estrutura.getLote().getDenominacaoImovel() : null,
                null != estrutura.getLote() ? estrutura.getLote().getArea() : null,
                null != estrutura.getLote() && null != estrutura.getLote().getMunicipio() ? estrutura.getLote().getMunicipio().getNome() : null,
                null != estrutura.getLote() && null != estrutura.getLote().getMunicipio() ? estrutura.getLote().getMunicipio().getId() : null,
                null != estrutura.getLote() && null != estrutura.getLote().getDistrito() ? estrutura.getLote().getDistrito().getId() : null

        );
    }

    public Estrutura toEntity(Lote lote, SituacaoJuridica situacaoJuridica, FormaObtencao forma) {
        BigDecimal areaRegistradaValue = null;
        BigDecimal areaMedidaValue = null;
        Date dataPosseDate = null;
        Date dataRegistroDate = null;

        try {
            if (null != areaRegistrada && !this.areaRegistrada.isBlank()) {
                areaRegistradaValue = new BigDecimal(this.areaRegistrada);
            }
            if (null != areaMedida && !this.areaMedida.isBlank()) {
                areaMedidaValue = new BigDecimal(this.areaMedida);
            }
            if (null != dataPosse && !this.dataPosse.isBlank() && !"null".equals(this.dataPosse)) {
                dataPosseDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.dataPosse);
            }
            if (null != dataRegistro && !this.dataRegistro.isBlank() && !"null".equals(this.dataRegistro)) {
                dataRegistroDate = new SimpleDateFormat("yyyy-MM-dd").parse(this.dataRegistro);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Erro ao converter valores para BigDecimal ou Date", e);
        }

        FormaObtencao formaObtencao = FormaObtencao.builder()
                .descricaoFormaDeObtencao(this.descricaoFormaDeObtencao)
                .oficio(this.oficio)
                .matricula(this.matricula)
                .livro(this.livro)
                .nomeCartorio(this.nomeCartorio)
                .dataRegistro(String.valueOf(dataRegistroDate))
                .numeroRegistro(this.numeroRegistro)
                .areaRegistrada(areaRegistradaValue)
                .areaMedida(areaMedidaValue)
                .municipioCartorio(this.municipioCartorio)
                .dataPosse(dataPosseDate)
                .numeroHerdeiros(this.numeroHerdeirosForma)
                .lote(lote)
                .situacaoJuridica(situacaoJuridica)
                .build();

        Estrutura estrutura = new Estrutura();
        estrutura.setId(this.id);
        estrutura.setLote(lote);
        estrutura.setDhc(this.dhc);
        estrutura.setDhm(this.dhm);
        estrutura.setFamiliasResidentes(this.familiasResidentes);
        estrutura.setPessoasResidentes(this.pessoasResidentes);
        estrutura.setTrabalhadoresComCarteira(this.trabalhadoresComCarteira);
        estrutura.setTrabalhadoresSemCarteira(this.trabalhadoresSemCarteira);
        estrutura.setMaoDeObraFamiliar(this.maoDeObraFamiliar);
        estrutura.setValorTotal(this.valorTotal);
        estrutura.setValorDasBenfeitorias(this.valorDasBenfeitorias);
        estrutura.setValorOutrasAtividades(this.valorOutrasAtividades);
        estrutura.setValorTerraNua(this.valorTerraNua);
        estrutura.setAreaIrrigada(this.areaIrrigada);
        estrutura.setLitigio(this.litigio);
        estrutura.setEntregouMemorialPlanilha(this.entregouMemorialPlanilha);
        estrutura.setDestinacaoDoImovel(this.destinacaoDoImovel);
        estrutura.setPontoDeReferencia(this.pontoDeReferencia);
        estrutura.setNumeroHerdeiros(this.numeroHerdeiros);
        estrutura.setObsLitigio(this.obsLitigio);
        estrutura.setPorcentagemDetencao(this.porcentagemDetencao);
        estrutura.setTipoEnergiaEletrica(this.tipoEnergiaEletrica);
        estrutura.setIsPossuiElergiaEletrica(this.isPossuiElergiaEletrica);
        estrutura.setIsPossuiEnergiaAlternativa(this.isPossuiEnergiaAlternativa);
        estrutura.setIsRedeDeAbastecimento(this.isRedeDeAbastecimento);
        estrutura.setIsIrrigacao(this.isIrrigacao);
        estrutura.setIsFonteAguaExterna(this.isFonteAguaExterna);
        estrutura.setIsRioOuRiacho(this.isRioOuRiacho);
        estrutura.setIsRioOuRiachoPerene(this.isRioOuRiachoPerene);
        estrutura.setIsAcude(this.isAcude);
        estrutura.setIsAcudePerene(this.isAcudePerene);
        estrutura.setIsOlhoDagua(this.isOlhoDagua);
        estrutura.setIsOlhoDaguaPerene(this.isOlhoDaguaPerene);
        estrutura.setIsLagoa(this.isLagoa);
        estrutura.setIsLagoaPerene(this.isLagoaPerene);
        estrutura.setIsPoco(this.isPoco);
        estrutura.setIsPocoPerene(this.isPocoPerene);
        estrutura.setUsoDaguaAcude(this.usoDaguaAcude);
        estrutura.setUsoDaguaLagoa(this.usoDaguaLagoa);
        estrutura.setUsoDaguaOlhoDagua(this.usoDaguaOlhoDagua);
        estrutura.setUsoDaguaPoco(this.usoDaguaPoco);
        estrutura.setUsoDaguaRioOuRiacho(this.usoDaguaRioOuRiacho);

        // associar forma obtencao se necessário externamente
        forma.setDescricaoFormaDeObtencao(formaObtencao.getDescricaoFormaDeObtencao());
        return estrutura;
    }

    private static String formatarData(Object v) {
        if (null == v) return null;
        if (v instanceof Date d) {
            return new SimpleDateFormat("yyyy-MM-dd").format(d);
        }
        // Se for String (coluna varchar), normalize se possível; senão devolva como veio
        String s = v.toString().trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;

        // tenta parsear alguns formatos comuns; se falhar, devolve original
        String[] patterns = {"yyyy-MM-dd", "dd/MM/yyyy", "yyyy-MM-dd HH:mm:ss", "EEE MMM dd HH:mm:ss zzz yyyy"};
        for (String p : patterns) {
            try {
                var d = new SimpleDateFormat(p, java.util.Locale.ENGLISH).parse(s);
                return new SimpleDateFormat("yyyy-MM-dd").format(d);
            } catch (Exception ignore) {}
        }
        return s; // mantém como está
    }
}

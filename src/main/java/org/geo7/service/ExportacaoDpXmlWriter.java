package org.geo7.service;

import org.geo7.dto.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ExportacaoDpXmlWriter {

    private ExportacaoDpXmlWriter() {}

    public static String buildMunicipioXml(Geo7MunicipioExportDTO dto) {
        String header = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        String open = "<exportacaoDP>";
        String close = "</exportacaoDP>";

        String body =
                "<_declaracoes>" +
                        "<imoveis>" +
                        dto.lotes().stream().map(l -> buildLoteXml(dto.municipio(), l)).collect(Collectors.joining()) +
                        "</imoveis>" +
                        "</_declaracoes>";

        return header + open + body + close;
    }

    private static String buildLoteXml(MunicipioDTO mun, Geo7LoteAgregado ag) {
        String numero = s(ag.lote().numero());
        String sncr = s(ag.lote().sncr());
        String cpf = s(ag.lote().cpf());

        String attrSncr = sncr.isEmpty() ? "" : " sncr=\"" + x(sncr) + "\"";
        String attrCpf  = cpf.isEmpty()  ? "" : " cpfLotes=\"" + x(cpf) + "\"";

        return
                "<lotes numeroLote=\"" + x(numero) + "\">" +
                        "<imovel numeroLote=\"" + x(numero) + "\"" + attrSncr + attrCpf + ">" +
                        buildDeclaracaoEstrutura(ag) +
                        buildDeclaracaoUso(mun, ag) +
                        buildDeclaracaoPessoa(ag) +
                        "</imovel>" +
                        "</lotes>";
    }

    private static String buildDeclaracaoEstrutura(Geo7LoteAgregado ag) {
        var e = ag.estrutura();
        var formas = ag.formas() != null ? ag.formas() : List.<FormaObtencaoDTO>of();

        var sb = new StringBuilder();
        sb.append("<declaracaoEstrutura>");
        tag(sb, "codigoCadastro", ag.lote().id());
        tag(sb, "proprietario", ag.lote().proprietario());
        tag(sb, "areaMedida", nf(e != null ? e.area() : ag.lote().area()));
        tag(sb, "denominacaoImovelRural", ag.lote().denominacaoImovel());
        tag(sb, "situacaoJuridica", ag.lote().situacaoJuridicaId());
        tag(sb, "indicacaoLocalizacao", ag.enderecoLote() != null ? ag.enderecoLote().pontoDeReferencia() : null);
        tag(sb, "codImoReceita", ag.enderecoLote() != null ? ag.enderecoLote().codImoReceita() : null);
        tag(sb, "localidade", ag.enderecoLote() != null ? ag.enderecoLote().localidade() : null);
        tag(sb, "nomeDistrito", ag.lote().distritoNome());

        if (e != null) {
            tag(sb, "familiasResidentes", nz(e.familiasResidentes()));
            tag(sb, "pessoasResidentes", nz(e.pessoasResidentes()));
            tag(sb, "trabalhadoresComCarteira", nz(e.trabalhadoresComCarteira()));
            tag(sb, "trabalhadoresSemCarteira", nz(e.trabalhadoresSemCarteira()));
            tag(sb, "maoObraFamiliar", nz(e.maoDeObraFamiliar()));

            tag(sb, "valorTotal", nf(e.valorTotal()));
            tag(sb, "valorBenfeitorias", nf(e.valorDasBenfeitorias()));
            tag(sb, "valorOutrasAtividades", nf(e.valorOutrasAtividades()));
            tag(sb, "valorTerraNua", nf(e.valorTerraNua()));

            tag(sb, "codigoDestinacaoDoImovel", e.destinacaoDoImovel());
            tag(sb, "codigoLitigio", e.litigio());
            tag(sb, "tipoEnergiaEletrica", e.tipoEnergiaEletrica());

            tag(sb, "isIrrigacao", sn(e.isIrrigacao()));
            tag(sb, "isPossuiEnergiaEletrica", sn(e.isPossuiElergiaEletrica()));
            tag(sb, "isPossuiEnergiaAlternativa", sn(e.isPossuiEnergiaAlternativa()));
            tag(sb, "isPossuiFonteDagua", sn(hasFonte(e)));
            tag(sb, "isPossuiFonteDaguaExterna", sn(e.isFonteAguaExterna()));

            tag(sb, "numeroHerdeiros", nz(e.numeroHerdeiros()));
            tag(sb, "isAcude", sn(e.isAcude()));
            tag(sb, "isAcudePerene", sn(e.isAcudePerene()));
            tag(sb, "isLagoa", sn(e.isLagoa()));
            tag(sb, "isLagoaPerene", sn(e.isLagoaPerene()));
            tag(sb, "isPoco", sn(e.isPoco()));
            tag(sb, "isPocoPerene", sn(e.isPocoPerene()));
            tag(sb, "isRioOuRiacho", sn(e.isRioOuRiacho()));
            tag(sb, "isOlhoDagua", sn(e.isOlhoDagua()));
            tag(sb, "isOlhoDaguaPerene", sn(e.isOlhoDaguaPerene()));
            tag(sb, "isRedeDeAbastecimento", sn(e.isRedeDeAbastecimento()));

            // … se precisar derivar usoAgua*, faça aqui
        }

        // formas de obtenção
        for (var f : formas) {
            sb.append("<formaObtencao>");
            tag(sb, "codigoCadastro", ag.lote().id());
            tag(sb, "areaFormaObtencaoMedida", nf(f.areaMedida()));
            tag(sb, "areaFormaObtencaoRegistrada", nf(f.areaRegistrada()));
            tag(sb, "codFormaObtencao", f.situacaoJuridicaId());
            tag(sb, "dataPosse", iso(f.dataPosse()));
            tag(sb, "dataRegistro", iso(f.dataRegistro()));
            tag(sb, "livro", f.livro());
            tag(sb, "matricula", f.matricula());
            tag(sb, "nomeCartorio", f.nomeCartorio());
            tag(sb, "numeroRegistro", f.numeroRegistro());
            tag(sb, "oficio", f.oficio());
            tag(sb, "registro", null);
            tag(sb, "municipioCartorio", f.municipioCartorio());
            sb.append("</formaObtencao>");
        }

        sb.append("</declaracaoEstrutura>");
        return sb.toString();
    }

    private static String buildDeclaracaoUso(MunicipioDTO mun, Geo7LoteAgregado ag) {
        var sb = new StringBuilder();
        var u = ag.dadosSobreUso();
        sb.append("<declaracaoUso uf=\"").append(x(s(mun.uf()))).append("\" municipio=\"").append(x(s(mun.nome()))).append("\">");
        tag(sb, "codMunicipio", 0); // preencha se tiver

        if (u != null && u.items() != null) {
            var itens = u.items();

            // separar por tipo (igual no front)
            var consorcio = itens.stream().filter(ExportacaoDpXmlWriter::isVegetalConsorcio).toList();
            var rotacao   = itens.stream().filter(ExportacaoDpXmlWriter::isVegetalRotacao).toList();
            var isolado   = itens.stream().filter(ExportacaoDpXmlWriter::isVegetalIsolado).toList();
            var granjeira = itens.stream().filter(i -> i.granjeiraAgricolaId() != null).toList();
            var outros    = itens.stream().filter(i -> i.areaComOutroUsoId() != null).toList();
            var restr     = itens.stream().filter(i -> i.areasRestricoesId() != null).toList();
            var pasto     = itens.stream().filter(i -> i.tipoPastagem() != null && !s(i.tipoPastagem()).isEmpty()).toList();
            var pec       = itens.stream().filter(i -> i.categoriaAnimalId() != null).toList();
            var semUso    = itens.stream().filter(i -> i.areaAproveitavelNaoUtilizada() != null).toList();

            sb.append("<vegetalConsorcio>");
            for (var it : consorcio) {
                sb.append("<item>");
                tag(sb, "categoriaIdVegetalConsorcio", it.categoriaId());
                tag(sb, "culturaIdVegetalConsorcio", it.culturaId());
                tag(sb, "formaExploracaoVegetalConsorcio", 6);
                tag(sb, "sequenciaProdutoVegetalConsorcio", nz(it.sequenciaProdutoVegetal()));
                tag(sb, "areaPlantadaVegetalConsorcio", nf(it.areaPlantada()));
                tag(sb, "areaColhidaVegetalConsorcio", nf(it.areaColhida()));
                tag(sb, "quantidadeColhidaVegetalConsorcio", s(it.quantidadeColhida()));
                tag(sb, "codigoUnidadeExploracaoIdVegetalConsorcio", s(it.codigoUnidadeProducao()));
                tag(sb, "indicadorGeralDeRestricaoVegetalConsorcio", s(it.indicadorGeralDeRestricao()));
                sb.append("</item>");
            }
            sb.append("</vegetalConsorcio>");

            sb.append("<vegetalIsolado>");
            for (var it : isolado) {
                sb.append("<item>");
                tag(sb, "categoriaIdVegetalIsolado", it.categoriaId());
                tag(sb, "culturaIdVegetalIsolado", it.culturaId());
                tag(sb, "areaPlantadaVegetalIsolado", nf(it.areaPlantada()));
                tag(sb, "areaColhidaVegetalIsolado", nf(it.areaColhida()));
                tag(sb, "quantidadeColhidaVegetalIsolado", s(it.quantidadeColhida()));
                tag(sb, "codigoUnidadeExploracaoIdVegetalIsolado", s(it.codigoUnidadeProducao()));
                tag(sb, "indicadorGeralDeRestricaoVegetalIsolado", s(it.indicadorGeralDeRestricao()));
                sb.append("</item>");
            }
            sb.append("</vegetalIsolado>");

            sb.append("<vegetalRotacao>");
            for (var it : rotacao) {
                sb.append("<item>");
                tag(sb, "categoriaIdVegetalRotacao", it.categoriaId());
                tag(sb, "culturaIdVegetalRotacao", it.culturaId());
                tag(sb, "formaExploracaoVegetalRotacao", 8);
                tag(sb, "sequenciaProdutoVegetalRotacao", nz(it.sequenciaProdutoVegetal()));
                tag(sb, "areaPlantadaVegetalRotacao", nf(it.areaPlantada()));
                tag(sb, "areaColhidaVegetalRotacao", nf(it.areaColhida()));
                tag(sb, "quantidadeColhidaVegetalRotacao", s(it.quantidadeColhida()));
                tag(sb, "codigoUnidadeExploracaoIdVegetalRotacao", s(it.codigoUnidadeProducao()));
                tag(sb, "indicadorGeralDeRestricaoVegetalRotacao", s(it.indicadorGeralDeRestricao()));
                sb.append("</item>");
            }
            sb.append("</vegetalRotacao>");

            sb.append("<areasGranjeira>");
            for (var g : granjeira) {
                sb.append("<item>");
                tag(sb, "categoriaIdGranjeiraAgricola", g.categoriaId());
                tag(sb, "granjeiraAgricolaId", g.granjeiraAgricolaId());
                tag(sb, "areaExploradaGranjeiraAgricola", nf(g.areaExploradaGranjeiraAgricola()));
                tag(sb, "indicadorRestricaoGranjeiraAgricola", s(g.indicadorGeralDeRestricao()));
                sb.append("</item>");
            }
            sb.append("</areasGranjeira>");

            sb.append("<areasOutrosUsos>");
            for (var ou : outros) {
                sb.append("<item>");
                tag(sb, "categoriaIdAreasOutrosUsos", ou.categoriaId());
                tag(sb, "culturaIdAreaOutrosUsos", ou.culturaId());
                tag(sb, "areaUtilizadaOutrosUsos", nf(ou.areaUtilizada()));
                tag(sb, "indicadorGeralDeRestricaoOutrosUsos", s(ou.indicadorGeralDeRestricao()));
                sb.append("</item>");
            }
            sb.append("<areaSemRestricao>");
            for (var ar : restr) {
                sb.append("<item>");
                tag(sb, "categoriaIdAreaInaproveitavel", ar.categoriaId());
                tag(sb, "areaInaproveitavelArea", nf(ar.areaUtilizadaRestricao()));
                sb.append("</item>");
            }
            sb.append("</areaSemRestricao>");
            sb.append("</areasOutrosUsos>");

            sb.append("<areaComPastagem>");
            for (var p : pasto) {
                sb.append("<item>");
                tag(sb, "categoriaIdAreasComPastagem", p.categoriaId());
                tag(sb, "tipoPastagem", normalizaPastagem(p.tipoPastagem()));
                tag(sb, "areaPastagem", nf(p.areaPastagem()));
                tag(sb, "indicadorGeralDeRestricaoPastagem", s(p.indicadorGeralDeRestricao()));
                sb.append("</item>");
            }
            sb.append("</areaComPastagem>");

            sb.append("<infoPecuaria>");
            for (var pc : pec) {
                sb.append("<item>");
                tag(sb, "categoriaId", pc.categoriaId());
                tag(sb, "categoriaAnimalId", pc.categoriaAnimalId());
                tag(sb, "quantidadeAnimal", nz(pc.quantidadeAnimal()));
                sb.append("</item>");
            }
            sb.append("</infoPecuaria>");

            sb.append("<areaSemRestricaoSemUso>");
            for (var sr : semUso) {
                sb.append("<item>");
                tag(sb, "categoriaIdAreasSemRestricaoSemUso", sr.categoriaId());
                tag(sb, "areaAproveitavelNaoUtilizada", nf(sr.areaAproveitavelNaoUtilizada()));
                sb.append("</item>");
            }
            sb.append("</areaSemRestricaoSemUso>");
        }

        sb.append("</declaracaoUso>");
        return sb.toString();
    }

    private static String buildDeclaracaoPessoa(Geo7LoteAgregado ag) {
        var sb = new StringBuilder("<declaracaoPessoa>");
        for (var px : ag.pessoas()) {
            var p = px.pessoa();
            var pl = px.pessoaLote();
            var end = px.endereco();
            var doc = px.documento();

            sb.append("<pessoa codigoCadastro=\"").append(x(s(p.id()))).append("\">");
            tag(sb, "nome", p.nome());
            tag(sb, "logradouro", end != null ? end.logradouro() : null);
            tag(sb, "numeroCasa", end != null ? end.numero() : "0");
            tag(sb, "complemento", end != null ? end.complemento() : null);
            tag(sb, "bairro", end != null ? end.bairro() : null);
            tag(sb, "nomeMunicipio", end != null ? end.municipioNome() : null);
            tag(sb, "uf", end != null ? end.uf() : null);
            tag(sb, "cep", end != null ? end.cep() : null);
            tag(sb, "ddd", null);
            tag(sb, "telefone", p.telefone());
            tag(sb, "isEspolio", sn(p.isEspolio()));

            tag(sb, "cpf", doc != null ? doc.cpf() : null);
            tag(sb, "dataNascimento", iso(p.dataNascimento()));
            tag(sb, "sexoPessoa", p.sexoPessoa());
            tag(sb, "estadoCivil", doc != null ? doc.estadoCivil() : "0");

            tag(sb, "nomeConjuge", null);
            tag(sb, "cpfConjuge", null);
            tag(sb, "rgConjuge", "0");
            tag(sb, "orgaoEmissorConjuge", null);
            tag(sb, "ufOrgaoEmissorConjuge", null);

            tag(sb, "tipoDocumentoIdentificacao", doc != null ? doc.tipoDocumentoIdentificacao() : "0");
            tag(sb, "numeroDocumentoIdentificacao", doc != null ? doc.numeroDocumentoIdentificacao() : null);
            tag(sb, "orgaoEmissor", doc != null ? doc.orgaoEmissor() : null);
            tag(sb, "ufOrgaoEmissor", doc != null ? doc.ufOrgaoEmissor() : null);

            tag(sb, "nacionalidade", doc != null ? doc.tipoNacionalidade() : "0");
            tag(sb, "municipioNacionalidade", null);
            tag(sb, "ufNaturalidade", end != null ? end.uf() : null);
            tag(sb, "nomePai", p.nomePai());
            tag(sb, "nomeMae", p.nomeMae());

            tag(sb, "condicaoPessoaImovelRural", pl != null ? pl.condicaoPessoaImovelRural() : "0");
            tag(sb, "isDeclarante", sn(pl != null ? pl.isDeclarante() : null));
            tag(sb, "isResideNoImovel", sn(pl != null ? pl.isResideNoImovel() : null));
            tag(sb, "percentDetencao", nf(pl != null ? pl.percentDetencao() : null));

            tag(sb, "tipoPessoa", doc != null ? doc.tipoPessoa() : "1");
            tag(sb, "cnpj", doc != null ? doc.cnpj() : null);
            tag(sb, "naturezaJuridica", doc != null ? doc.naturezaJuridica() : "0");
            tag(sb, "ufPaisSede", doc != null ? doc.ufPaisSede() : null);
            tag(sb, "capitalNacional", nz(doc != null ? doc.capitalNacional() : null));
            tag(sb, "capitalEstrangeiro", nz(doc != null ? doc.capitalEstrangeiro() : null));

            tag(sb, "dataCasamento", iso(p.dataCasamento()));
            tag(sb, "regimeDeBens", p.regimeDeBens() != null ? p.regimeDeBens() : "0");
            tag(sb, "ordem", "1");

            tag(sb, "coordenadaEste", p.coordenadaEste());
            tag(sb, "coordenadaNorte", p.coordenadaNorte());

            tag(sb, "isRecebePronaf", sn(p.isRecebePronaf()));
            tag(sb, "isRecebeAjudoProgramaGoverno", sn(p.isRecebeAjudoProgramaGoverno()));
            tag(sb, "atividadePrincipalExploracao", pl != null ? pl.atividadePrincipalExploracao() : null);

            tag(sb, "valorTotalPronafs", nf(p.valorTotalPronafs()));
            tag(sb, "atividadePrincipal", p.atividadePrincipal());
            tag(sb, "qtdPronaf", nz(p.qtdPronaf()));
            sb.append("</pessoa>");
        }
        sb.append("</declaracaoPessoa>");
        return sb.toString();
    }

    // ===== Helpers (iguais aos do front) =====
    private static void tag(StringBuilder sb, String name, Object value) {
        String v = s(value);
        if (v.isEmpty()) {
            sb.append("<").append(name).append("/>");
        } else {
            sb.append("<").append(name).append(">").append(x(v)).append("</").append(name).append(">");
        }
    }
    private static String s(Object v) { return v == null ? "" : String.valueOf(v); }
    private static String x(String v) {
        return v.replace("&","&amp;").replace("<","&lt;")
                .replace(">","&gt;").replace("\"","&quot;").replace("'","&apos;");
    }
    private static String nf(Object v) {
        if (v == null) return "0.0000";
        try { return String.format(java.util.Locale.US, "%.4f", Double.parseDouble(String.valueOf(v))); }
        catch (Exception e) { return "0.0000"; }
    }
    private static String sn(Object v) {
        if (v instanceof String s) {
            String t = s.trim().toLowerCase();
            if (List.of("sim","true","1","s","y","yes").contains(t)) return "SIM";
            if (List.of("nao","não","false","0","n","não","no").contains(t)) return "NÃO";
        }
        return (v != null && !Objects.equals(v, false) && !Objects.equals(v, 0)) ? "SIM" : "NÃO";
    }
    private static String iso(Object v) {
        if (v == null) return "";
        try {
            var d = (v instanceof java.util.Date) ? (java.util.Date) v
                    : java.sql.Date.valueOf(String.valueOf(v));
            var cal = java.util.Calendar.getInstance();
            cal.setTime(d);
            var yyyy = cal.get(java.util.Calendar.YEAR);
            var MM = cal.get(java.util.Calendar.MONTH)+1;
            var dd = cal.get(java.util.Calendar.DAY_OF_MONTH);
            return String.format("%04d-%02d-%02dT00:00:00", yyyy, MM, dd);
        } catch (Exception e) {
            return "";
        }
    }
    private static int nz(Number n) { return n == null ? 0 : n.intValue(); }
    private static boolean hasFonte(EstruturaDTO e) {
        return e != null && (Boolean.TRUE.equals(e.isRioOuRiacho()) || Boolean.TRUE.equals(e.isAcude())
                || Boolean.TRUE.equals(e.isOlhoDagua()) || Boolean.TRUE.equals(e.isLagoa())
                || Boolean.TRUE.equals(e.isPoco()));
    }
    private static String normalizaPastagem(Object v) {
        String t = s(v).toUpperCase();
        if (List.of("1","NATURAL","NATIVA").contains(t)) return "NATURAL";
        if (List.of("3","PLANTADA","CULTIVADA").contains(t)) return "PLANTADA";
        return "NATURAL";
    }

    // predicados de vegetal (copiando a lógica do front)
    private static boolean isVegetal(ItemDTO i) { return i.culturaId() != null; }
    private static int forma(ItemDTO i) {
        String t = s(i.formaExploracao()).toUpperCase();
        if (List.of("6","CONSORCIO","CONSÓRCIO").contains(t)) return 6;
        if (List.of("8","ROTACAO","ROTAÇÃO").contains(t)) return 8;
        return 7; // isolado
    }
    static boolean isVegetalConsorcio(ItemDTO i){ return isVegetal(i) && forma(i)==6; }
    static boolean isVegetalRotacao(ItemDTO i){ return isVegetal(i) && forma(i)==8; }
    static boolean isVegetalIsolado(ItemDTO i){ return isVegetal(i) && forma(i)!=6 && forma(i)!=8; }
}


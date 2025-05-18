package org.geo7;

import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@SpringBootApplication
public class Geo7Application {


//    @Bean
//    public CommandLineRunner run(
//            @Autowired LoteRepository loteRepository,
//            @Autowired FormaObtencaoRepository formaObtencaoRepository,
//            @Autowired EstruturaRepository estruturaRepository,
//            @Autowired EnderecoLoteRepository enderecoLoteRepository,
//            @Autowired DistritoRepository distritoRepository
//            ){
//        return args -> {
//
////
//            SituacaoJuridica possePorSimplesOcupacao = new SituacaoJuridica();
//            possePorSimplesOcupacao.setId(1L);
//            possePorSimplesOcupacao.setNome("Posse Por Simples Ocupacao");
//
//            SituacaoJuridica posseAJustoTitulo = new SituacaoJuridica();
//            posseAJustoTitulo.setId(2L);
//            posseAJustoTitulo.setNome("Posse a Justo Título");
//
//            SituacaoJuridica dominio = new SituacaoJuridica();
//            dominio.setId(3L);
//            dominio.setNome("Área Registrada (Domínio)");
//
//            SituacaoJuridica indefinido = new SituacaoJuridica();
//            indefinido.setId(3L);
//            indefinido.setNome("Indefinido");
//
//            Municipio municipio = new Municipio();
//            municipio.setId(2300200L);
//
//            Lote lote = new Lote();
//            lote.setArea(BigDecimal.valueOf(6.0000));
//            lote.setCpf("00000000006");
//            lote.setDenominacaoImovel("Imovel teste 6");
//            lote.setNumero("0004");
//            lote.setPerimetro(20.0000);
//            lote.setProprietario("Proprietario teste 5");
//            lote.setSncr("12345678099");
//            lote.setMunicipio(municipio);
//            lote.setSituacaoJuridica(possePorSimplesOcupacao);
//
//            loteRepository.save(lote);
//
//            // Busca um distrito existente
//            String codigoDistrito = "230020005";
//            Distrito distrito = distritoRepository.findByCodigoDistrito(codigoDistrito)
//                    .orElseThrow(() -> new RuntimeException("Distrito não encontrado: " + codigoDistrito));
//
//            EnderecoLote enderecoLote = new EnderecoLote();
//            enderecoLote.setLote(lote);
//            enderecoLote.setDistrito(distrito);
//            enderecoLote.setPontoDeReferencia("BR 403 ACARAÚ A TRIANGULO DO MARCO A 13 KM A DIREITA A 1 KM");
//
//            enderecoLoteRepository.save(enderecoLote);
//
//            System.out.println("EnderecoLote salvo com sucesso!");
//
//
//            FormaObtencao formaObtencao = new FormaObtencao();
//            formaObtencao.setDescricaoFormaDeObtencao("07 - Compra e Venda de Particular");
//            formaObtencao.setAreaMedida(BigDecimal.valueOf(10.000));
//            formaObtencao.setSituacaoJuridica(dominio);
//            formaObtencao.setMunicipioCartorio("Acaraú");
//            formaObtencao.setLote(lote);
//
//            formaObtencaoRepository.save(formaObtencao);
//
//            Estrutura estrutura = new Estrutura();
//            estrutura.setLote(lote);
//            estrutura.setAreaIrrigada(BigDecimal.valueOf(1.0000));
//            estrutura.setIsAcude(true);
//            estrutura.setIsPoco(true);
//            estrutura.setFamiliasResidentes(3);
//            estrutura.setPontoDeReferencia("PONTO DE REFERENCIA TESTE");
//
//            estruturaRepository.save(estrutura);
//
//
//
//        };
//    }

    public static void main(String[] args) {

        SpringApplication.run(Geo7Application.class, args);

    }
}

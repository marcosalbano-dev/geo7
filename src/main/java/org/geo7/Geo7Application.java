package org.geo7;

import org.geo7.model.entity.*;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.SituacaoJuridicaRepository;
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


    @Bean
    public CommandLineRunner run(@Autowired LoteRepository loteRepository){
        return args -> {

//
            SituacaoJuridica situacaoJuridica = new SituacaoJuridica();
            situacaoJuridica.setId(2L);
            situacaoJuridica.setNome("Posse a Justo Título");

            Municipio municipio = new Municipio();
            municipio.setId(2300101L);

            Lote lote = new Lote();
            lote.setArea(BigDecimal.valueOf(4.0000));
            lote.setCpf("41622995368");
            lote.setDenominacaoImovel("Imovel teste 2");
            lote.setNumero("0001");
            lote.setPerimetro(12.0000);
            lote.setProprietario("Proprietario teste");
            lote.setSncr("1234567890");
            lote.setMunicipio(municipio);
            lote.setSituacaoJuridica(situacaoJuridica);
            loteRepository.save(lote);
        };
    }

    public static void main(String[] args) {

        SpringApplication.run(Geo7Application.class, args);

    }
}

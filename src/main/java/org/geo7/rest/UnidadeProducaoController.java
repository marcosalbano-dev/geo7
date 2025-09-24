package org.geo7.rest;

import lombok.RequiredArgsConstructor;
import org.geo7.model.entity.UnidadeProducao;
import org.geo7.model.repository.UnidadeProducaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/unidades-producao")
@RequiredArgsConstructor
public class UnidadeProducaoController {
    @Autowired
    private final UnidadeProducaoRepository unidadeProducaoRepository;

    @GetMapping
    public List<UnidadeProducao> listar(){ return unidadeProducaoRepository.findAll(); }

    @GetMapping("/search")
    public List<UnidadeProducao> search(@RequestParam String q){
        return unidadeProducaoRepository.findByUnidadeContainingIgnoreCase(q);
    }

}


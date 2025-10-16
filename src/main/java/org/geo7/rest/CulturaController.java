package org.geo7.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.geo7.model.entity.Cultura;
import org.geo7.model.repository.CulturaRepository;
import org.geo7.dto.CulturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/culturas")
@RequiredArgsConstructor
public class CulturaController {

    @Autowired
    private CulturaRepository culturaRepository;

    @GetMapping
    public List<CulturaDTO> listar() {
        return culturaRepository.findAllByOrderByNomeCulturaAsc()
                .stream().map(CulturaDTO::fromEntity).toList();
    }

    @GetMapping("{id}")
    public CulturaDTO porId(@PathVariable Long id) {
        return culturaRepository.findById(id).map(CulturaDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Cultura não encontrada"));
    }
}



package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.SituacaoJuridica;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.SituacaoJuridicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/situacaoJuridica")
public class SituacaoJuridicaController {

    @Autowired
    private SituacaoJuridicaRepository situacaoJuridicaRepository;

    public SituacaoJuridicaController(SituacaoJuridicaRepository situacaoJuridicaRepository) {
        this.situacaoJuridicaRepository = situacaoJuridicaRepository;
    }

    @GetMapping("{id}")
    public ResponseEntity<SituacaoJuridica> buscarPorId(@PathVariable Long id) {
        return situacaoJuridicaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "SituacaoJuridica não encontrada"));
    }

}

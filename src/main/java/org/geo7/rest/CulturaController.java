package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Cultura;
import org.geo7.model.repository.CulturaRepository;
import org.geo7.rest.dto.CulturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/culturas")
public class CulturaController {

    @Autowired
    private CulturaRepository culturaRepository;

    @PostMapping
    public ResponseEntity<CulturaDTO> salvar(@RequestBody @Valid CulturaDTO dto) {
        Cultura cultura = dto.toEntity();
        Cultura salvo = culturaRepository.save(cultura);
        return ResponseEntity.ok(CulturaDTO.fromEntity(salvo));
    }

    @GetMapping("/{codigoCultura}")
    public ResponseEntity<CulturaDTO> buscarPorId(@PathVariable Integer codigoCultura) {
        return culturaRepository.findByCodigoCultura(codigoCultura)
                .map(CulturaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultura não encontrada"));
    }

    @GetMapping
    public ResponseEntity<List<CulturaDTO>> listarTodos() {
        List<CulturaDTO> culturas = culturaRepository.findAll().stream()
                .map(CulturaDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(culturas);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CulturaDTO> atualizar(@PathVariable Long id, @RequestBody @Valid CulturaDTO dto) {
        return culturaRepository.findById(id)
                .map(culturaExistente -> {
                    Cultura atualizado = dto.toEntity();
                    atualizado.setId(id);
                    return ResponseEntity.ok(CulturaDTO.fromEntity(culturaRepository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultura não encontrada"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        return culturaRepository.findById(id)
                .map(cultura -> {
                    culturaRepository.delete(cultura);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultura não encontrada"));
    }
}

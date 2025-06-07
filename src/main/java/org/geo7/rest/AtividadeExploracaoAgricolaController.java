package org.geo7.rest;

import jakarta.validation.Valid;

import org.geo7.model.entity.AtividadeExploracaoAgricola;
import org.geo7.model.repository.AtividadeExploracaoAgricolaRepository;
import org.geo7.rest.dto.AtividadeExploracaoAgricolaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/atividades-exploracao")
public class AtividadeExploracaoAgricolaController {

    @Autowired
    private AtividadeExploracaoAgricolaRepository repository;

    @PostMapping
    public ResponseEntity<AtividadeExploracaoAgricolaDTO> salvar(@RequestBody @Valid AtividadeExploracaoAgricolaDTO dto) {
        AtividadeExploracaoAgricola entity = dto.toEntity();
        AtividadeExploracaoAgricola salvo = repository.save(entity);
        return ResponseEntity.ok(AtividadeExploracaoAgricolaDTO.fromEntity(salvo));
    }

    @GetMapping("{id}")
    public ResponseEntity<AtividadeExploracaoAgricolaDTO> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(AtividadeExploracaoAgricolaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atividade de exploração agrícola não encontrada"));
    }

    @GetMapping
    public ResponseEntity<List<AtividadeExploracaoAgricolaDTO>> listarTodos() {
        List<AtividadeExploracaoAgricolaDTO> lista = repository.findAll()
                .stream()
                .map(AtividadeExploracaoAgricolaDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<AtividadeExploracaoAgricolaDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AtividadeExploracaoAgricolaDTO dto) {
        return repository.findById(id)
                .map(existente -> {
                    AtividadeExploracaoAgricola atualizado = dto.toEntity();
                    atualizado.setId(id);
                    return ResponseEntity.ok(AtividadeExploracaoAgricolaDTO.fromEntity(repository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atividade de exploração agrícola não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(entidade -> {
                    repository.delete(entidade);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Atividade de exploração agrícola não encontrada"));
    }
}

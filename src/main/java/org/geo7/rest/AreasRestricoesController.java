package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.AreasRestricoes;
import org.geo7.model.repository.AreasRestricoesRepository;
import org.geo7.rest.dto.AreasRestricoesDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/areas-restricoes")
public class AreasRestricoesController {

    @Autowired
    private AreasRestricoesRepository repository;

    @PostMapping
    public ResponseEntity<AreasRestricoesDTO> salvar(@RequestBody @Valid AreasRestricoesDTO dto) {
        AreasRestricoes entity = dto.toEntity();
        AreasRestricoes salvo = repository.save(entity);
        return ResponseEntity.ok(AreasRestricoesDTO.fromEntity(salvo));
    }

    @GetMapping("{id}")
    public ResponseEntity<AreasRestricoesDTO> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(AreasRestricoesDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área com restrição não encontrada"));
    }

    @GetMapping
    public ResponseEntity<List<AreasRestricoesDTO>> listarTodos() {
        List<AreasRestricoesDTO> list = repository.findAll()
                .stream()
                .map(AreasRestricoesDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }

    @PutMapping("{id}")
    public ResponseEntity<AreasRestricoesDTO> atualizar(@PathVariable Long id, @RequestBody @Valid AreasRestricoesDTO dto) {
        return repository.findById(id)
                .map(entity -> {
                    AreasRestricoes atualizado = dto.toEntity();
                    atualizado.setId(id);
                    return ResponseEntity.ok(AreasRestricoesDTO.fromEntity(repository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área com restrição não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(entity -> {
                    repository.delete(entity);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área com restrição não encontrada"));
    }
}

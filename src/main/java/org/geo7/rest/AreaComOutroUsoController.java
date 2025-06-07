package org.geo7.rest;

import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/area-com-outro-uso")
public class AreaComOutroUsoController {

    @Autowired
    private AreaComOutroUsoRepository repository;

    @PostMapping
    public ResponseEntity<AreaComOutroUsoDTO> salvar(@RequestBody AreaComOutroUsoDTO dto) {
        AreaComOutroUso salvo = repository.save(dto.toEntity());
        return ResponseEntity.ok(AreaComOutroUsoDTO.fromEntity(salvo));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<AreaComOutroUsoDTO> buscarPorId(@PathVariable String codigo) {
        return repository.findByCodigo(codigo)
                .map(AreaComOutroUsoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<AreaComOutroUsoDTO>> listarTodos() {
        List<AreaComOutroUsoDTO> lista = repository.findAll()
                .stream()
                .map(AreaComOutroUsoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<AreaComOutroUsoDTO> atualizar(@PathVariable Long id, @RequestBody AreaComOutroUsoDTO dto) {
        return repository.findById(id)
                .map(registro -> {
                    AreaComOutroUso atualizado = dto.toEntity();
                    atualizado.setId(id);
                    return ResponseEntity.ok(AreaComOutroUsoDTO.fromEntity(repository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(r -> {
                    repository.delete(r);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Registro não encontrado"));
    }
}

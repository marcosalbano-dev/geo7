package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Categoria;
import org.geo7.model.repository.CategoriaRepository;
import org.geo7.rest.dto.CategoriaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository repository;

    @PostMapping
    public ResponseEntity<CategoriaDTO> salvar(@RequestBody @Valid CategoriaDTO dto) {
        Categoria entity = dto.toEntity();
        Categoria salvo = repository.save(entity);
        return ResponseEntity.ok(CategoriaDTO.fromEntity(salvo));
    }

    @GetMapping("{id}")
    public ResponseEntity<CategoriaDTO> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(CategoriaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> listarTodos() {
        List<CategoriaDTO> lista = repository.findAll()
                .stream()
                .map(CategoriaDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoriaDTO> atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaDTO dto) {
        return repository.findById(id)
                .map(existente -> {
                    Categoria atualizado = dto.toEntity();
                    atualizado.setId(id);
                    return ResponseEntity.ok(CategoriaDTO.fromEntity(repository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(entidade -> {
                    repository.delete(entidade);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
    }
}

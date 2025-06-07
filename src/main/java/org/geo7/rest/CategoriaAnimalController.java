package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.CategoriaAnimal;
import org.geo7.model.repository.CategoriaAnimalRepository;
import org.geo7.rest.dto.CategoriaAnimalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-animais")
public class CategoriaAnimalController {

    @Autowired
    private CategoriaAnimalRepository repository;

    @PostMapping
    public ResponseEntity<CategoriaAnimalDTO> salvar(@RequestBody @Valid CategoriaAnimalDTO dto) {
        CategoriaAnimal entity = dto.toEntity();
        CategoriaAnimal salvo = repository.save(entity);
        return ResponseEntity.ok(CategoriaAnimalDTO.fromEntity(salvo));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<CategoriaAnimalDTO> buscarPorId(@PathVariable String codigo) {
        return repository.findByCodigo(codigo)
                .map(CategoriaAnimalDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria de animal não encontrada"));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaAnimalDTO>> listarTodos() {
        List<CategoriaAnimalDTO> lista = repository.findAll()
                .stream()
                .map(CategoriaAnimalDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<CategoriaAnimalDTO> atualizar(@PathVariable Long id, @RequestBody @Valid CategoriaAnimalDTO dto) {
        return repository.findById(id)
                .map(existente -> {
                    CategoriaAnimal atualizado = dto.toEntity();
                    atualizado.setId(id);
                    return ResponseEntity.ok(CategoriaAnimalDTO.fromEntity(repository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria de animal não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return repository.findById(id)
                .map(entidade -> {
                    repository.delete(entidade);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria de animal não encontrada"));
    }
}

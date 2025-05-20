package org.geo7.rest;

import org.geo7.model.entity.SituacaoJuridica;
import org.geo7.model.repository.SituacaoJuridicaRepository;
import org.geo7.rest.dto.SituacaoJuridicaDTO;
import org.geo7.rest.mapper.SituacaoJuridicaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/situacaoJuridica")
public class SituacaoJuridicaController {

    @Autowired
    private SituacaoJuridicaRepository repository;

    @GetMapping
    public List<SituacaoJuridicaDTO> getAll() {
        return repository.findAll()
                .stream()
                .map(SituacaoJuridicaMapper::toDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SituacaoJuridicaDTO> getById(@PathVariable Long id) {
        SituacaoJuridica entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não encontrado"));
        return ResponseEntity.ok(SituacaoJuridicaMapper.toDTO(entity));
    }

    @PostMapping
    public ResponseEntity<SituacaoJuridicaDTO> create(@RequestBody SituacaoJuridicaDTO dto) {
        SituacaoJuridica entity = SituacaoJuridicaMapper.toEntity(dto);
        SituacaoJuridica saved = repository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).body(SituacaoJuridicaMapper.toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SituacaoJuridicaDTO> update(@PathVariable Long id, @RequestBody SituacaoJuridicaDTO dto) {
        SituacaoJuridica existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Não encontrado"));

        existing.setNome(dto.nome());
        SituacaoJuridica updated = repository.save(existing);

        return ResponseEntity.ok(SituacaoJuridicaMapper.toDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Não encontrado");
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
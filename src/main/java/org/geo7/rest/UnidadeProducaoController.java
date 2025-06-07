package org.geo7.rest;

import lombok.RequiredArgsConstructor;
import org.geo7.model.entity.UnidadeProducao;
import org.geo7.model.repository.UnidadeProducaoRepository;
import org.geo7.rest.dto.UnidadeProducaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/unidade-producao")
@RequiredArgsConstructor
public class UnidadeProducaoController {
    @Autowired
    private final UnidadeProducaoRepository unidadeProducaoRepository;

    @GetMapping
    public ResponseEntity<List<UnidadeProducaoDTO>> findAll() {
        List<UnidadeProducaoDTO> list = unidadeProducaoRepository.findAll().stream()
                .map(UnidadeProducaoDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{codigoUnidade}")
    public ResponseEntity<UnidadeProducaoDTO> findById(@PathVariable String codigoUnidade) {
        return unidadeProducaoRepository.findByCodigoUnidade(codigoUnidade)
                .map(UnidadeProducaoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UnidadeProducaoDTO> create(@RequestBody UnidadeProducaoDTO dto) {
        UnidadeProducao entity = dto.toEntity();
        UnidadeProducao saved = unidadeProducaoRepository.save(entity);
        return ResponseEntity
                .created(URI.create("/api/unidade-producao/" + saved.getId()))
                .body(UnidadeProducaoDTO.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UnidadeProducaoDTO> update(@PathVariable Long id, @RequestBody UnidadeProducaoDTO dto) {
        return unidadeProducaoRepository.findById(id)
                .map(existing -> {
                    UnidadeProducao updated = dto.toEntity();
                    updated.setId(id);
                    UnidadeProducao saved = unidadeProducaoRepository.save(updated);
                    return ResponseEntity.ok(UnidadeProducaoDTO.fromEntity(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return unidadeProducaoRepository.findById(id)
                .map(entity -> {
                    unidadeProducaoRepository.delete(entity);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}


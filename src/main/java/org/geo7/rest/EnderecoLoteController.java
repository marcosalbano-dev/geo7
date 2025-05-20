package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.EnderecoLote;
import org.geo7.model.entity.Lote;
import org.geo7.model.repository.EnderecoLoteRepository;
import org.geo7.model.repository.LoteRepository;
import org.geo7.rest.dto.EnderecoLoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/enderecoLote")
public class EnderecoLoteController {

    @Autowired
    private EnderecoLoteRepository enderecoLoteRepository;

    @GetMapping("/{id}")
    public ResponseEntity<EnderecoLoteDTO> getById(@PathVariable Long id) {
        return enderecoLoteRepository.findById(id)
                .map(EnderecoLoteDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EnderecoLoteDTO> create(@RequestBody EnderecoLoteDTO dto) {
        EnderecoLote enderecoLote = dto.toEntity();
        EnderecoLote saved = enderecoLoteRepository.save(enderecoLote);
        return ResponseEntity.ok(EnderecoLoteDTO.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EnderecoLoteDTO> update(@PathVariable Long id, @RequestBody EnderecoLoteDTO dto) {
        if (!enderecoLoteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        EnderecoLote enderecoLote = dto.toEntity();
        enderecoLote.setId(id); // Garante que o ID seja o mesmo do path
        EnderecoLote saved = enderecoLoteRepository.save(enderecoLote);
        return ResponseEntity.ok(EnderecoLoteDTO.fromEntity(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (enderecoLoteRepository.existsById(id)) {
            enderecoLoteRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

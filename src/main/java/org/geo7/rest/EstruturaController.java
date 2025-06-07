package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Estrutura;
import org.geo7.model.entity.Lote;
import org.geo7.model.repository.EstruturaRepository;
import org.geo7.model.repository.LoteRepository;
import org.geo7.rest.dto.EstruturaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estrutura")
@CrossOrigin(origins = "http://localhost:4200")
public class EstruturaController {

    private final EstruturaRepository estruturaRepository;
    private final LoteRepository loteRepository;

    public EstruturaController(EstruturaRepository estruturaRepository,
                               LoteRepository loteRepository) {
        this.estruturaRepository = estruturaRepository;
        this.loteRepository = loteRepository;
    }

    @GetMapping
    public ResponseEntity<List<EstruturaDTO>> findAll() {
        List<EstruturaDTO> estruturas = estruturaRepository.findAll()
                .stream()
                .map(EstruturaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estruturas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstruturaDTO> findById(@PathVariable Long id) {
        return estruturaRepository.findById(id)
                .map(EstruturaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id));
    }

    @PostMapping
    public ResponseEntity<EstruturaDTO> create(@Valid @RequestBody EstruturaDTO dto) {
        Lote lote = loteRepository.findById(dto.loteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Lote não encontrado com id: " + dto.loteId()));

        Estrutura estrutura = dto.toEntity(lote);
        Estrutura saved = estruturaRepository.save(estrutura);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EstruturaDTO.fromEntity(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstruturaDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EstruturaDTO dto) {

        return estruturaRepository.findById(id)
                .map(existingEstrutura -> {
                    Lote lote = loteRepository.findById(dto.loteId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "Lote não encontrado com id: " + dto.loteId()));

                    Estrutura updatedEstrutura = dto.toEntity(lote);
                    updatedEstrutura.setId(id);
                    Estrutura saved = estruturaRepository.save(updatedEstrutura);
                    return ResponseEntity.ok(EstruturaDTO.fromEntity(saved));
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (estruturaRepository.existsById(id)) {
            estruturaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id);
    }

    @GetMapping("/por-lote/{loteId}")
    public ResponseEntity<List<EstruturaDTO>> findByLoteId(@PathVariable Long loteId) {
        List<EstruturaDTO> estruturas = estruturaRepository.findByLoteId(loteId)
                .stream()
                .map(EstruturaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estruturas);
    }
}

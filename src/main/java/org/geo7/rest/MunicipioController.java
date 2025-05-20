package org.geo7.rest;

import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.Municipio;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.MunicipioRepository;
import org.geo7.rest.dto.MunicipioDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/muncipios")
public class MunicipioController {

    @Autowired
    private MunicipioRepository municipioRepository;

    @PostMapping
    public ResponseEntity<MunicipioDTO> create(@Valid @RequestBody MunicipioDTO dto) {
        Municipio municipio = dto.toEntity();
        Municipio saved = municipioRepository.save(municipio);
        return ResponseEntity.status(HttpStatus.CREATED).body(MunicipioDTO.fromEntity(saved));
    }

    @GetMapping("/{id}")
    public ResponseEntity<MunicipioDTO> findById(@PathVariable Long id) {
        Municipio municipio = municipioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Município não encontrado"));
        return ResponseEntity.ok(MunicipioDTO.fromEntity(municipio));
    }

    @GetMapping
    public List<MunicipioDTO> findAll() {
        return municipioRepository.findAll()
                .stream()
                .map(MunicipioDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<MunicipioDTO> update(@PathVariable Long id, @Valid @RequestBody MunicipioDTO dto) {
        return municipioRepository.findById(id)
                .map(municipioExistente -> {
                    Municipio atualizado = dto.toEntity();
                    atualizado.setId(id);
                    Municipio saved = municipioRepository.save(atualizado);
                    return ResponseEntity.ok(MunicipioDTO.fromEntity(saved));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Município não encontrado"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return municipioRepository.findById(id)
                .map(municipio -> {
                    municipioRepository.delete(municipio);
                    return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Município não encontrado"));
    }
}

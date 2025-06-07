package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.GranjeiraAgricola;
import org.geo7.model.repository.GranjeiraAgricolaRepository;
import org.geo7.rest.dto.GranjeiraAgricolaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/granjeiras-agricolas")
public class GranjeiraAgricolaController {

    @Autowired
    private GranjeiraAgricolaRepository granjeiraAgricolaRepository;

    @PostMapping
    public ResponseEntity<GranjeiraAgricolaDTO> salvar(@RequestBody @Valid GranjeiraAgricolaDTO dto) {
        GranjeiraAgricola entidade = dto.toEntity();
        GranjeiraAgricola salvo = granjeiraAgricolaRepository.save(entidade);
        return ResponseEntity.ok(GranjeiraAgricolaDTO.fromEntity(salvo));
    }

    @GetMapping("{codigo}")
    public ResponseEntity<GranjeiraAgricolaDTO> buscarPorId(@PathVariable Integer codigo) {
        return granjeiraAgricolaRepository.findByCodigo(codigo)
                .map(GranjeiraAgricolaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Granjeira agrícola não encontrada"));
    }

    @GetMapping
    public ResponseEntity<List<GranjeiraAgricolaDTO>> listarTodas() {
        List<GranjeiraAgricolaDTO> lista = granjeiraAgricolaRepository.findAll()
                .stream()
                .map(GranjeiraAgricolaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<GranjeiraAgricolaDTO> atualizar(@PathVariable Long id, @RequestBody @Valid GranjeiraAgricolaDTO dto) {
        return granjeiraAgricolaRepository.findById(id)
                .map(existente -> {
                    GranjeiraAgricola atualizado = dto.toEntity();
                    atualizado.setId(id);
                    return ResponseEntity.ok(GranjeiraAgricolaDTO.fromEntity(granjeiraAgricolaRepository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Granjeira agrícola não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return granjeiraAgricolaRepository.findById(id)
                .map(entidade -> {
                    granjeiraAgricolaRepository.delete(entidade);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Granjeira agrícola não encontrada"));
    }
}

package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Distrito;
import org.geo7.model.entity.Lote;
import org.geo7.model.repository.DistritoRepository;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.MunicipioRepository;
import org.geo7.rest.dto.DistritoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/distritos")
public class DistritoController {

    @Autowired
    private final DistritoRepository distritoRepository;
    @Autowired
    private final MunicipioRepository municipioRepository;

    public DistritoController(DistritoRepository distritoRepository, MunicipioRepository municipioRepository) {
        this.distritoRepository = distritoRepository;
        this.municipioRepository = municipioRepository;
    }

    @PostMapping
    public ResponseEntity<DistritoDTO> salvar(@Valid @RequestBody DistritoDTO dto) {
        var municipio = municipioRepository.findById(dto.municipioId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Município não encontrado"));

        Distrito salvo = distritoRepository.save(dto.toEntity(municipio));
        return ResponseEntity.ok(DistritoDTO.fromEntity(salvo));
    }

    @GetMapping("{codigoDistrito}")
    public ResponseEntity<DistritoDTO> buscarPorId(@PathVariable String codigoDistrito) {
        return distritoRepository.findByCodigoDistrito(codigoDistrito)
                .map(DistritoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Distrito não encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<DistritoDTO>> listarPorMunicipio(@RequestParam(required = false) Long municipioId) {
        List<Distrito> distritos;
        if (municipioId != null) {
            distritos = distritoRepository.findByMunicipioId(municipioId);
        } else {
            distritos = distritoRepository.findAll();
        }
        List<DistritoDTO> dtoList = distritos.stream()
                .map(DistritoDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(dtoList);
    }
    @PutMapping("{id}")
    public ResponseEntity<DistritoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid DistritoDTO dto) {
        return distritoRepository.findById(id)
                .map(d -> {
                    var municipio = municipioRepository.findById(dto.municipioId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Município não encontrado"));

                    d.setCodigoDistrito(dto.codigoDistrito());
                    d.setNomeDistrito(dto.nomeDistrito());
                    d.setMunicipio(municipio);
                    return ResponseEntity.ok(DistritoDTO.fromEntity(distritoRepository.save(d)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Distrito não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return distritoRepository.findById(id)
                .map(d -> {
                    distritoRepository.delete(d);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Distrito não encontrado"));
    }
}

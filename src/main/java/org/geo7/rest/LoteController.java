package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.Municipio;
import org.geo7.model.entity.SituacaoJuridica;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.MunicipioRepository;
import org.geo7.model.repository.SituacaoJuridicaRepository;
import org.geo7.rest.dto.LoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private SituacaoJuridicaRepository situacaoJuridicaRepository;

    public LoteController(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }


    @PostMapping
    public ResponseEntity<LoteDTO> salvar(@RequestBody LoteDTO dto) {
        Municipio municipio = municipioRepository.findById(dto.municipioId())
                .orElseThrow(() -> new RuntimeException("Município não encontrado"));

        SituacaoJuridica sj = dto.situacaoJuridicaId() != null ? situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                .orElseThrow(() -> new RuntimeException("Situação jurídica não encontrada")) : null;

        Set<FormaObtencao> formas = dto.formaObtencao().stream()
                .map(f -> {
                    SituacaoJuridica sjForma = situacaoJuridicaRepository.findById(f.situacaoJuridicaId())
                            .orElseThrow(() -> new RuntimeException("Situação jurídica de forma de obtenção inválida"));
                    return f.toEntity(null, sjForma); // lote será setado depois
                })
                .collect(Collectors.toSet());

        Lote lote = dto.toEntity(municipio, sj, formas);

        Lote salvo = loteRepository.save(lote);
        return ResponseEntity.ok(LoteDTO.fromEntity(salvo));
    }

    @GetMapping("{id}")
    public ResponseEntity<LoteDTO> buscarPorId(@PathVariable Long id) {
        return loteRepository.findById(id)
                .map(LoteDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<LoteDTO>> listarTodos() {
        List<LoteDTO> lotes = loteRepository.findAll()
                .stream()
                .map(LoteDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lotes);
    }

    @PutMapping("{id}")
    public ResponseEntity<LoteDTO> atualizar(@PathVariable Long id, @RequestBody @Valid LoteDTO dto) {
        return loteRepository.findById(id)
                .map(loteExistente -> {
                    Municipio municipio = municipioRepository.findById(dto.municipioId())
                            .orElseThrow(() -> new RuntimeException("Município não encontrado"));

                    SituacaoJuridica sj = dto.situacaoJuridicaId() != null
                            ? situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                            .orElseThrow(() -> new RuntimeException("Situação jurídica não encontrada"))
                            : null;

                    Set<FormaObtencao> formas = dto.formaObtencao().stream()
                            .map(f -> {
                                SituacaoJuridica sjForma = situacaoJuridicaRepository.findById(f.situacaoJuridicaId())
                                        .orElseThrow(() -> new RuntimeException("Situação jurídica inválida"));
                                return f.toEntity(null, sjForma); // vínculo ao lote é feito em toEntity()
                            })
                            .collect(Collectors.toSet());

                    Lote atualizado = dto.toEntity(municipio, sj, formas);
                    atualizado.setId(id); // garante atualização
                    return ResponseEntity.ok(LoteDTO.fromEntity(loteRepository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return loteRepository.findById(id)
                .map(l -> {
                    loteRepository.delete(l);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }
}

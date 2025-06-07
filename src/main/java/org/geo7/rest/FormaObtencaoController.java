package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.SituacaoJuridica;
import org.geo7.model.repository.FormaObtencaoRepository;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.SituacaoJuridicaRepository;
import org.geo7.rest.dto.FormaObtencaoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/forma-obtencao")
public class FormaObtencaoController {

    @Autowired
    private FormaObtencaoRepository formaObtencaoRepository;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private SituacaoJuridicaRepository situacaoJuridicaRepository;

    @PostMapping
    public ResponseEntity<FormaObtencaoDTO> salvar(@RequestBody @Valid FormaObtencaoDTO dto) {
        Lote lote = loteRepository.findById(dto.loteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));

        SituacaoJuridica sj = situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Situação jurídica não encontrada"));

        FormaObtencao forma = dto.toEntity(lote, sj);
        FormaObtencao salva = formaObtencaoRepository.save(forma);
        return ResponseEntity.ok(FormaObtencaoDTO.fromEntity(salva));
    }

    @GetMapping("{id}")
    public ResponseEntity<FormaObtencaoDTO> buscarPorId(@PathVariable Long id) {
        return formaObtencaoRepository.findById(id)
                .map(FormaObtencaoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Forma de obtenção não encontrada"));
    }

    @GetMapping
    public ResponseEntity<List<FormaObtencaoDTO>> listarTodas() {
        List<FormaObtencaoDTO> lista = formaObtencaoRepository.findAll()
                .stream()
                .map(FormaObtencaoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<FormaObtencaoDTO> atualizar(@PathVariable Long id, @RequestBody @Valid FormaObtencaoDTO dto) {
        return formaObtencaoRepository.findById(id)
                .map(existente -> {
                    Lote lote = loteRepository.findById(dto.loteId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));

                    SituacaoJuridica sj = situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Situação jurídica não encontrada"));

                    FormaObtencao atualizado = dto.toEntity(lote, sj);
                    atualizado.setId(id);
                    return ResponseEntity.ok(FormaObtencaoDTO.fromEntity(formaObtencaoRepository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Forma de obtenção não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return formaObtencaoRepository.findById(id)
                .map(f -> {
                    formaObtencaoRepository.delete(f);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Forma de obtenção não encontrada"));
    }
}

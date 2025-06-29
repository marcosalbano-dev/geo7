package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.Pessoa;
import org.geo7.model.entity.PessoaLote;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.PessoaLoteRepository;
import org.geo7.model.repository.PessoaRepository;
import org.geo7.rest.dto.PessoaLoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pessoa-lote")
@CrossOrigin(origins = "http://localhost:4200")
public class PessoaLoteController {

    @Autowired
    private PessoaLoteRepository pessoaLoteRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private LoteRepository loteRepository;

    @PostMapping
    public ResponseEntity<PessoaLoteDTO> salvar(@Valid @RequestBody PessoaLoteDTO dto) {
        // Busca as entidades relacionadas pelo id do DTO
        Pessoa pessoa = pessoaRepository.findById(dto.pessoaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pessoa não encontrada"));
        Lote lote = loteRepository.findById(dto.loteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lote não encontrado"));

        PessoaLote pessoaLote = dto.toEntity(pessoa, lote);

        pessoaLoteRepository.save(pessoaLote);

        return ResponseEntity
                .created(URI.create("/api/pessoa-lote/" + pessoaLote.getId()))
                .body(PessoaLoteDTO.fromEntity(pessoaLote));
    }

    @GetMapping("{id}")
    public ResponseEntity<PessoaLoteDTO> buscarPorId(@PathVariable Long id) {
        return pessoaLoteRepository.findById(id)
                .map(PessoaLoteDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PessoaLote não encontrado"));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<PessoaLoteDTO>> listarTodos() {
        List<PessoaLoteDTO> lista = pessoaLoteRepository.findAll()
                .stream()
                .map(PessoaLoteDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<PessoaLoteDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PessoaLoteDTO dto) {
        return pessoaLoteRepository.findById(id)
                .map(plExistente -> {
                    Pessoa pessoa = pessoaRepository.findById(dto.pessoaId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pessoa não encontrada"));
                    Lote lote = loteRepository.findById(dto.loteId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lote não encontrado"));

                    PessoaLote atualizado = dto.toEntity(pessoa, lote);
                    atualizado.setId(id); // garante atualização
                    pessoaLoteRepository.save(atualizado);

                    return ResponseEntity.ok(PessoaLoteDTO.fromEntity(atualizado));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PessoaLote não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return pessoaLoteRepository.findById(id)
                .map(pl -> {
                    pessoaLoteRepository.delete(pl);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "PessoaLote não encontrado"));
    }
}

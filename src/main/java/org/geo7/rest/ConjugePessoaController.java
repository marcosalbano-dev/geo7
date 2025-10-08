package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.dto.ConjugePessoaDTO;
import org.geo7.model.entity.ConjugePessoa;
import org.geo7.model.entity.Municipio;
import org.geo7.model.entity.Pessoa;
import org.geo7.model.repository.ConjugePessoaRepository;
import org.geo7.model.repository.MunicipioRepository;
import org.geo7.model.repository.PessoaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/conjuge-pessoa")
@CrossOrigin(origins = "http://localhost:4200")
public class ConjugePessoaController {

    @Autowired
    private ConjugePessoaRepository conjugePessoaRepository;

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @PostMapping
    public ResponseEntity<ConjugePessoaDTO> salvar(@Valid @RequestBody ConjugePessoaDTO dto) {
        Pessoa pessoa = pessoaRepository.findById(
                        Optional.ofNullable(dto.pessoaId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "pessoaId é obrigatório"))
                )
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pessoa não encontrada"));

        Municipio municipioResidencia = null;
        if (dto.municipioResidenciaId() != null) {
            municipioResidencia = municipioRepository.findById(dto.municipioResidenciaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Município de residência não encontrado"));
        }

        Municipio municipioNaturalidade = null;
        if (dto.municipioNaturalidadeId() != null) {
            municipioNaturalidade = municipioRepository.findById(dto.municipioNaturalidadeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Município de naturalidade não encontrado"));
        }

        ConjugePessoa entidade = dto.toEntity(pessoa, municipioResidencia, municipioNaturalidade);
        conjugePessoaRepository.save(entidade);

        return ResponseEntity
                .created(URI.create("/api/conjuge-pessoa/" + entidade.getId()))
                .body(ConjugePessoaDTO.fromEntity(entidade));
    }

    @GetMapping("{id}")
    public ResponseEntity<ConjugePessoaDTO> buscarPorId(@PathVariable Long id) {
        return conjugePessoaRepository.findById(id)
                .map(ConjugePessoaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConjugePessoa não encontrado"));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<ConjugePessoaDTO>> listarTodos() {
        List<ConjugePessoaDTO> lista = conjugePessoaRepository.findAll()
                .stream()
                .map(ConjugePessoaDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<ConjugePessoaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ConjugePessoaDTO dto) {
        return conjugePessoaRepository.findById(id)
                .map(existente -> {
                    // Pessoa é obrigatória para o vínculo
                    Pessoa pessoa = pessoaRepository.findById(
                                    Optional.ofNullable(dto.pessoaId())
                                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "pessoaId é obrigatório"))
                            )
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pessoa não encontrada"));

                    Municipio municipioResidencia = null;
                    if (dto.municipioResidenciaId() != null) {
                        municipioResidencia = municipioRepository.findById(dto.municipioResidenciaId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Município de residência não encontrado"));
                    }

                    Municipio municipioNaturalidade = null;
                    if (dto.municipioNaturalidadeId() != null) {
                        municipioNaturalidade = municipioRepository.findById(dto.municipioNaturalidadeId())
                                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Município de naturalidade não encontrado"));
                    }

                    ConjugePessoa atualizado = dto.toEntity(pessoa, municipioResidencia, municipioNaturalidade);
                    atualizado.setId(id);
                    conjugePessoaRepository.save(atualizado);

                    return ResponseEntity.ok(ConjugePessoaDTO.fromEntity(atualizado));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConjugePessoa não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return conjugePessoaRepository.findById(id)
                .map(ent -> {
                    conjugePessoaRepository.delete(ent);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ConjugePessoa não encontrado"));
    }

    // Conveniência: buscar cônjuge por pessoa (muitas vezes há 0..1 registro por pessoa)
    @GetMapping("/por-pessoa/{pessoaId}")
    @Transactional(readOnly = true)
    public ResponseEntity<List<ConjugePessoaDTO>> listarPorPessoa(@PathVariable Long pessoaId) {
        if (!pessoaRepository.existsById(pessoaId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada");
        }
        var lista = conjugePessoaRepository.findByPessoaId(pessoaId)
                .stream()
                .map(ConjugePessoaDTO::fromEntity)
                .toList();
        if (lista.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }
}

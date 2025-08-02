package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Pessoa;
//import org.geo7.model.entity.ProgramaGoverno;
import org.geo7.model.entity.Pronaf;
import org.geo7.model.repository.PessoaRepository;
//import org.geo7.model.repository.ProgramaGovernoRepository;
import org.geo7.rest.dto.AtualizaDetentorRequestDTO;
import org.geo7.rest.dto.PessoaDTO;
import org.geo7.rest.dto.PronafDTO;
import org.geo7.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pessoas")
@CrossOrigin(origins = "http://localhost:4200")
public class PessoaController {

    private final PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;

//    @Autowired
//    private ProgramaGovernoRepository programaRepo;


    public PessoaController(PessoaRepository pessoaRepository) {
        this.pessoaRepository = pessoaRepository;
    }


    @GetMapping
    public ResponseEntity<List<PessoaDTO>> findAll() {
        List<PessoaDTO> pessoas = pessoaRepository.findAll()
                .stream()
                .map(PessoaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(pessoas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDTO> findById(@PathVariable Long id) {
        return pessoaRepository.findById(id)
                .map(PessoaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pessoa não encontrada com id: " + id));
    }

//    @PostMapping
//    public ResponseEntity<PessoaDTO> salvar(@Valid @RequestBody PessoaDTO dto, PronafDTO pronafDTO) {
//        // Se precisar buscar entidades relacionadas (ex: município), busque aqui
//        Pessoa pessoa = dto.toEntity();
//        pessoa = pessoaRepository.save(pessoa);
//        return ResponseEntity.created(URI.create("/api/pessoas/" + pessoa.getId()))
//                .body(PessoaDTO.fromEntity(pessoa));
//    }

    @PostMapping
    public ResponseEntity<PessoaDTO> criarPessoaDetentor(@RequestBody AtualizaDetentorRequestDTO dto) {
        try {
            pessoaService.salvaDetentor(dto); // Você pode criar este método parecido com o atualizaDetentor
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Erro ao criar detentor: " + e.getMessage());

        }

    }

    @PutMapping("/{pessoaLoteId}")
    public ResponseEntity<PessoaDTO> atualizarPessoaDetentor(
            @PathVariable Long pessoaLoteId,
            @RequestBody AtualizaDetentorRequestDTO dto) {
        try {
            pessoaService.atualizaDetentor(pessoaLoteId, dto);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Erro ao atualizar detentor: " + e.getMessage());
        }
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<PessoaDTO> atualizar(@PathVariable Long id, @Valid @RequestBody PessoaDTO dto) {
//        return pessoaRepository.findById(id)
//                .map(existing -> {
//                    Pessoa updated = dto.toEntity();
//                    updated.setId(id);
//                    updated = pessoaRepository.save(updated);
//                    return ResponseEntity.ok(PessoaDTO.fromEntity(updated));
//                })
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "Pessoa não encontrada com id: " + id));
//    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PessoaDTO> delete(@PathVariable Long id) {
        if (pessoaRepository.existsById(id)) {
            pessoaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada com id: " + id);
    }
}

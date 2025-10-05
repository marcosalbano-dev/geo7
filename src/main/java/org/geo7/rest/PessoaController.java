package org.geo7.rest;

import org.geo7.model.repository.PessoaRepository;
import org.geo7.dto.AtualizaDetentorRequestDTO;
import org.geo7.dto.EditarDetentorResponseDTO;
import org.geo7.dto.PessoaDTO;
import org.geo7.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pessoas")
@CrossOrigin(origins = "http://localhost:4200")
public class PessoaController {

    private final PessoaRepository pessoaRepository;

    @Autowired
    private PessoaService pessoaService;


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

    @PostMapping
    public ResponseEntity<PessoaDTO> criarPessoaDetentor(@RequestBody AtualizaDetentorRequestDTO dto) {
        try {
            return ResponseEntity.ok(pessoaService.salvaDetentor(dto));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Erro ao criar detentor: " + e.getMessage());

        }

    }

    @GetMapping("/editar/{pessoaLoteId}")
    public ResponseEntity<EditarDetentorResponseDTO> buscarParaEdicao(@PathVariable Long pessoaLoteId) {
        return pessoaService.getEditarDetentorData(pessoaLoteId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "PessoaLote não encontrado: " + pessoaLoteId));
    }


    @PutMapping("/{pessoaLoteId}")
    public ResponseEntity<EditarDetentorResponseDTO> atualizarPessoaDetentor(
            @PathVariable Long pessoaLoteId,
            @RequestBody AtualizaDetentorRequestDTO dto) {
        try {
            return ResponseEntity.ok(pessoaService.atualizaDetentor(pessoaLoteId, dto));
        } catch (Exception e) {
            e.printStackTrace();
            throw new ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "Erro ao atualizar detentor: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<PessoaDTO> delete(@PathVariable Long id) {
        if (pessoaRepository.existsById(id)) {
            pessoaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada com id: " + id);
    }

    @GetMapping("/editar/por-lote/{loteId}")
    public ResponseEntity<EditarDetentorResponseDTO> editarPorLote(@PathVariable Long loteId) {
        return pessoaService.getEditarDetentorPorLote(loteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

}

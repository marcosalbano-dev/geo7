package org.geo7.rest;

import org.geo7.model.entity.DadosSobreUso;
import org.geo7.model.repository.DadosSobreUsoRepository;
import org.geo7.rest.dto.DadosSobreUsoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dados-sobre-uso")
public class DadosSobreUsoController {

    @Autowired
    private DadosSobreUsoRepository dadosSobreUsoRepository;

    @PostMapping
    public ResponseEntity<DadosSobreUsoDTO> salvar(@RequestBody DadosSobreUsoDTO dto) {
        DadosSobreUso entidade = dto.toEntity();
        DadosSobreUso salvo = dadosSobreUsoRepository.save(entidade);
        return ResponseEntity.ok(DadosSobreUsoDTO.fromEntity(salvo));
    }

    @GetMapping("{id}")
    public ResponseEntity<DadosSobreUsoDTO> buscarPorId(@PathVariable Long id) {
        return dadosSobreUsoRepository.findById(id)
                .map(DadosSobreUsoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DadosSobreUso não encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<DadosSobreUsoDTO>> listarTodos() {
        List<DadosSobreUsoDTO> lista = dadosSobreUsoRepository.findAll()
                .stream()
                .map(DadosSobreUsoDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<DadosSobreUsoDTO> atualizar(@PathVariable Long id, @RequestBody DadosSobreUsoDTO dto) {
        return dadosSobreUsoRepository.findById(id)
                .map(entidadeExistente -> {
                    DadosSobreUso atualizado = dto.toEntity();
                    atualizado.setId(id);
                    DadosSobreUso salvo = dadosSobreUsoRepository.save(atualizado);
                    return ResponseEntity.ok(DadosSobreUsoDTO.fromEntity(salvo));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DadosSobreUso não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return dadosSobreUsoRepository.findById(id)
                .map(entidade -> {
                    dadosSobreUsoRepository.delete(entidade);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DadosSobreUso não encontrado"));
    }
}

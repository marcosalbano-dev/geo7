package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Estrutura;
import org.geo7.model.entity.Lote;
import org.geo7.model.repository.EstruturaRepository;
import org.geo7.model.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/estrutura")
public class EstruturaController {

    @Autowired
    private EstruturaRepository estruturaRepository;

    public EstruturaController(EstruturaRepository estruturaRepository) {
        this.estruturaRepository = estruturaRepository;
    }

    @PostMapping
    public ResponseEntity<Estrutura> salvar(@Valid @RequestBody Estrutura estrutura) {
        return ResponseEntity.ok(estruturaRepository.save(estrutura));
    }

    @GetMapping("{id}")
    public ResponseEntity<Estrutura> buscarPorId(@PathVariable Long id) {
        return estruturaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estrutura não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return estruturaRepository.findById(id)
                .map(estrutura -> {
                    estruturaRepository.delete(estrutura);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("{id}")
    public ResponseEntity<Estrutura> atualizar(@Valid @PathVariable Long id, @RequestBody Estrutura estruturaAtualizada) {
        return estruturaRepository.findById(id)
                .map(estrutura -> {
                    estrutura.setPontoDeReferencia(estruturaAtualizada.getPontoDeReferencia());
                    estrutura.setLote(estruturaAtualizada.getLote());
                    estrutura.setPontoDeReferencia(estruturaAtualizada.getPontoDeReferencia());
                    estruturaRepository.save(estrutura);
                    return ResponseEntity.ok(estrutura);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

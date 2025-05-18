package org.geo7.rest;

import org.geo7.model.entity.Lote;
import org.geo7.model.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    @Autowired
    private LoteRepository loteRepository;

    public LoteController(LoteRepository loteRepository) {
        this.loteRepository = loteRepository;
    }

    @PostMapping
    public ResponseEntity<Lote> salvar(@RequestBody Lote lote) {
        Lote salvo = loteRepository.save(lote);
        return ResponseEntity.ok(salvo);
    }

    @GetMapping("{id}")
    public ResponseEntity<Lote> buscarPorId(@PathVariable Long id) {
        return loteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return loteRepository.findById(id)
                .map(lote -> {
                    loteRepository.delete(lote);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }
    @PutMapping("{id}")
    public ResponseEntity<Lote> atualizar(@PathVariable Long id, @RequestBody Lote loteAtualizado) {
        return loteRepository.findById(id)
                .map(lote -> {
                    lote.setSncr(loteAtualizado.getSncr());
                    lote.setProprietario(loteAtualizado.getProprietario());
                    lote.setArea(loteAtualizado.getArea());
                    lote.setDenominacaoImovel(loteAtualizado.getDenominacaoImovel());
                    lote.setSituacaoJuridica(loteAtualizado.getSituacaoJuridica());
                    lote.setMunicipio(loteAtualizado.getMunicipio());
                    loteRepository.save(lote);
                    return ResponseEntity.ok(lote);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

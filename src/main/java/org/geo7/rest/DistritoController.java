package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.Distrito;
import org.geo7.model.entity.Lote;
import org.geo7.model.repository.DistritoRepository;
import org.geo7.model.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/disritos")
public class DistritoController {

    @Autowired
    private DistritoRepository distritoRepository;
    public DistritoController(DistritoRepository distritoRepository) {
        this.distritoRepository = distritoRepository;
    }

    @PostMapping
    public ResponseEntity<Distrito> salvar(@Valid @RequestBody Distrito distrito) {
        return ResponseEntity.ok(distritoRepository.save(distrito));
    }

    @GetMapping("{id}")
    public ResponseEntity<Distrito> buscarPorId(@PathVariable Long id) {
        return distritoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Distrito não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return distritoRepository.findById(id)
                .map(distrito -> {
                    distritoRepository.delete(distrito);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("{id}")
    public ResponseEntity<Distrito> atualizar(@Valid @PathVariable Long id, @RequestBody Distrito distritoAtualizado) {
        return distritoRepository.findById(id)
                .map(distrito -> {
                    distrito.setCodigoDistrito(distritoAtualizado.getCodigoDistrito());
                    distrito.setNomeDistrito(distritoAtualizado.getNomeDistrito());
                    distrito.setMunicipio(distritoAtualizado.getMunicipio());
                    distritoRepository.save(distrito);
                    return ResponseEntity.ok(distrito);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}

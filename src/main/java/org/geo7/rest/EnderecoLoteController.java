package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.EnderecoLote;
import org.geo7.model.entity.Lote;
import org.geo7.model.repository.EnderecoLoteRepository;
import org.geo7.model.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/enderecoLote")
public class EnderecoLoteController {

    @Autowired
    private EnderecoLoteRepository enderecoLoteRepository;

    public EnderecoLoteController(EnderecoLoteRepository enderecoLoteRepository) {
        this.enderecoLoteRepository = enderecoLoteRepository;
    }

    @PostMapping
    public ResponseEntity<EnderecoLote> salvar(@Valid @RequestBody EnderecoLote enderecoLote) {
        return ResponseEntity.ok(enderecoLoteRepository.save(enderecoLote));
    }

    @GetMapping("{id}")
    public ResponseEntity<EnderecoLote> buscarPorId(@PathVariable Long id) {
        return enderecoLoteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "EnderecoLote não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return enderecoLoteRepository.findById(id)
                .map(enderecoLote -> {
                    enderecoLoteRepository.delete(enderecoLote);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("{id}")
    public ResponseEntity<EnderecoLote> atualizar(@Valid @PathVariable Long id, @RequestBody EnderecoLote enderecoLoteAtualizado) {
        return enderecoLoteRepository.findById(id)
                .map(enderecoLote -> {
                    enderecoLote.setPontoDeReferencia(enderecoLoteAtualizado.getPontoDeReferencia());
                    enderecoLote.setDistrito(enderecoLoteAtualizado.getDistrito());
                    enderecoLote.setLote(enderecoLoteAtualizado.getLote());
                    enderecoLote.setComunidade(enderecoLoteAtualizado.getComunidade());
                    enderecoLote.setLocalidade(enderecoLoteAtualizado.getLocalidade());
                    enderecoLoteRepository.save(enderecoLote);
                    return ResponseEntity.ok(enderecoLote);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}

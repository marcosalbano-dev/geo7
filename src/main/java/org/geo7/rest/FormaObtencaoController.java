package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.repository.FormaObtencaoRepository;
import org.geo7.model.repository.LoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/formaObtencao")
public class FormaObtencaoController {

    @Autowired
    private FormaObtencaoRepository formaObtencaoRepository;

    public FormaObtencaoController(final FormaObtencaoRepository formaObtencaoRepository) {
        this.formaObtencaoRepository = formaObtencaoRepository;
    }

    @PostMapping
    public ResponseEntity<FormaObtencao> salvar(@Valid @RequestBody FormaObtencao formaObtencao) {
        return ResponseEntity.ok(formaObtencaoRepository.save(formaObtencao));
    }

    @GetMapping("{id}")
    public ResponseEntity<FormaObtencao> buscarPorId(@PathVariable Long id) {
        return formaObtencaoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FormaObtencao não encontrada"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return formaObtencaoRepository.findById(id)
                .map(formaObtencao -> {
                    formaObtencaoRepository.delete(formaObtencao);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.notFound().build());

    }

    @PutMapping("{id}")
    public ResponseEntity<FormaObtencao> atualizar(@Valid @PathVariable Long id, @RequestBody FormaObtencao formaObtencaoAtualizada) {
        return formaObtencaoRepository.findById(id)
                .map(formaObtencao -> {
                    formaObtencao.setDescricaoFormaDeObtencao(formaObtencaoAtualizada.getDescricaoFormaDeObtencao());
                    formaObtencao.setSituacaoJuridica(formaObtencaoAtualizada.getSituacaoJuridica());
                    formaObtencao.setMunicipioCartorio(formaObtencaoAtualizada.getMunicipioCartorio());
                    formaObtencao.setAreaRegistrada(formaObtencaoAtualizada.getAreaRegistrada());
                    formaObtencaoRepository.save(formaObtencao);
                    return ResponseEntity.ok(formaObtencao);
                })
                .orElse(ResponseEntity.notFound().build());
    }

}

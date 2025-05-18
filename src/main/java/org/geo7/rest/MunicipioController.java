package org.geo7.rest;

import jakarta.persistence.Entity;
import jakarta.validation.Valid;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.Municipio;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.MunicipioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/muncipios")
public class MunicipioController {

    @Autowired
    private MunicipioRepository municipioRepository;

    public MunicipioController(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    @GetMapping("{id}")
    public ResponseEntity<Municipio> buscarPorId(@PathVariable Long id) {
        return municipioRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Município não encontrado"));
    }
}

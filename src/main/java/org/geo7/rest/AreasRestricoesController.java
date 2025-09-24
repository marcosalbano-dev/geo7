package org.geo7.rest;

import lombok.RequiredArgsConstructor;
import org.geo7.dto.AreasRestricoesDTO;
import org.geo7.model.entity.AreasRestricoes;
import org.geo7.model.repository.AreasRestricoesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/areas-restricoes")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AreasRestricoesController {

    @Autowired
    private AreasRestricoesRepository areasRestricoesRepository;

    @GetMapping
    public List<AreasRestricoesDTO> listar() {
        return areasRestricoesRepository.findAllByOrderByIdAsc().stream()
                .map(AreasRestricoesDTO::fromEntity).toList();
    }
}

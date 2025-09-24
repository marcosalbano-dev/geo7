package org.geo7.rest;

import lombok.RequiredArgsConstructor;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/areas-com-outro-uso")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class AreaComOutroUsoController {

    @Autowired
    private AreaComOutroUsoRepository areaComOutroUsoRepository;

    @GetMapping
    public List<AreaComOutroUsoDTO> listar() {
        return areaComOutroUsoRepository.findAllByOrderByIdAsc().stream()
                .map(AreaComOutroUsoDTO::fromEntity).toList();
    }
}

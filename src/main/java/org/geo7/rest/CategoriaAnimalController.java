package org.geo7.rest;

import lombok.RequiredArgsConstructor;
import org.geo7.dto.CategoriaAnimalDTO;
import org.geo7.model.repository.CategoriaAnimalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias-animal")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class CategoriaAnimalController {

    @Autowired
    private CategoriaAnimalRepository categoriaAnimalRepository;

    @GetMapping
    public List<CategoriaAnimalDTO> listar() {
        return categoriaAnimalRepository.findAllByOrderByIdAsc().stream()
                .map(CategoriaAnimalDTO::fromEntity).toList();
    }
}

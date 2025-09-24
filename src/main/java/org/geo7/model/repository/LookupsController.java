package org.geo7.model.repository;

import lombok.RequiredArgsConstructor;
import org.geo7.model.entity.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/lookups")
@RequiredArgsConstructor
public class LookupsController {

    private final AreaComOutroUsoRepository outroRepo;
    private final AreasRestricoesRepository restrRepo;
    private final CategoriaRepository catRepo;
    private final CategoriaAnimalRepository catAnRepo;
    private final CulturaRepository culturaRepo;

    @GetMapping("/outros-usos") public List<AreaComOutroUso> outrosUsos(){ return outroRepo.findAll(); }
    @GetMapping("/restricoes")  public List<AreasRestricoes> restr(){ return restrRepo.findAll(); }
    @GetMapping("/categorias")  public List<Categoria> categorias(){ return catRepo.findAll(); }
    @GetMapping("/categorias-animal") public List<CategoriaAnimal> categoriasAnimal(){ return catAnRepo.findAll(); }
    @GetMapping("/culturas")    public List<Cultura> culturas(){ return culturaRepo.findAll(); }
}

package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.rest.dto.ItemDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/itens")
public class ItemController {

    @Autowired private ItemRepository itemRepository;
    @Autowired private CategoriaRepository categoriaRepository;
    @Autowired private CulturaRepository culturaRepository;
    @Autowired private UnidadeProducaoRepository unidadeProducaoRepository;
    @Autowired private GranjeiraAgricolaRepository granjeiraAgricolaRepository;
    @Autowired private AreaComOutroUsoRepository areaComOutroUsoRepository;
    @Autowired private AreasRestricoesRepository areasRestricoesRepository;
    @Autowired private CategoriaAnimalRepository categoriaAnimalRepository;
    @Autowired private LoteRepository loteRepository;

    @PostMapping
    public ResponseEntity<ItemDTO> salvar(@RequestBody @Valid ItemDTO dto) {
        Item item = mapDtoToEntity(dto);
        item = itemRepository.save(item);
        return ResponseEntity.ok(ItemDTO.fromEntity(item));
    }

    @GetMapping("{id}")
    public ResponseEntity<ItemDTO> buscarPorId(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(ItemDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
    }

    @GetMapping
    public ResponseEntity<List<ItemDTO>> listarTodos() {
        List<ItemDTO> lista = itemRepository.findAll()
                .stream()
                .map(ItemDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("{id}")
    public ResponseEntity<ItemDTO> atualizar(@PathVariable Long id, @RequestBody @Valid ItemDTO dto) {
        return itemRepository.findById(id)
                .map(existente -> {
                    Item atualizado = mapDtoToEntity(dto);
                    atualizado.setId(id);
                    return ResponseEntity.ok(ItemDTO.fromEntity(itemRepository.save(atualizado)));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return itemRepository.findById(id)
                .map(entidade -> {
                    itemRepository.delete(entidade);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado"));
    }

    private Item mapDtoToEntity(ItemDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.categoriaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        Lote lote = loteRepository.findById(dto.loteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
        Cultura cultura = culturaRepository.findById(dto.culturaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultura não encontrada"));
        UnidadeProducao unidade = unidadeProducaoRepository.findByCodigoUnidade(dto.codigoUnidadeProducao())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade de Produção não encontrada"));
        GranjeiraAgricola granjeira = granjeiraAgricolaRepository.findById(dto.granjeiraAgricolaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Granjeira Agrícola não encontrada"));
        AreaComOutroUso areaComOutroUso = areaComOutroUsoRepository.findById(dto.areaComOutroUsoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área com Outro Uso não encontrada"));
        AreasRestricoes areasRestricoes = areasRestricoesRepository.findById(dto.areasRestricoesId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Áreas com Restrições não encontradas"));
        CategoriaAnimal categoriaAnimal = categoriaAnimalRepository.findById(dto.categoriaAnimalId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria Animal não encontrada"));

        return dto.toEntity(categoria, lote, cultura, unidade, granjeira, areaComOutroUso, areasRestricoes, categoriaAnimal);
    }
}

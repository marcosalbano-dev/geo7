package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.dto.ItemDTO;
import org.geo7.service.ItemService;
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

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CategoriaRepository categoriaRepository;
    @Autowired
    private CulturaRepository culturaRepository;
    @Autowired
    private UnidadeProducaoRepository unidadeProducaoRepository;
    @Autowired
    private GranjeiraAgricolaRepository granjeiraAgricolaRepository;
    @Autowired
    private AreaComOutroUsoRepository areaComOutroUsoRepository;
    @Autowired
    private AreasRestricoesRepository areasRestricoesRepository;
    @Autowired
    private CategoriaAnimalRepository categoriaAnimalRepository;
    @Autowired
    private LoteRepository loteRepository;
    @Autowired
    private ItemService itemService;


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

    private UnidadeProducao resolveUnidade(ItemDTO dto) {
        if (dto.unidadeProducaoId() != null) {
            return unidadeProducaoRepository.findById(dto.unidadeProducaoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unidade de Produção (ID) não encontrada"));
        }
        if (dto.codigoUnidadeProducao() != null && !dto.codigoUnidadeProducao().isBlank()) {
            return unidadeProducaoRepository.findByCodigoUnidade(dto.codigoUnidadeProducao().trim())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de Unidade de Produção inválido"));
        }
        return null; // opcional
    }

    private Item mapDtoToEntity(ItemDTO dto) {
        UnidadeProducao up = null;
        Item item = new Item();
        item.setId(dto.id());

        if (dto.unidadeProducaoId() != null) {
            up = unidadeProducaoRepository.findByCodigoUnidade(dto.codigoUnidadeProducao())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade de produção não encontrada"));
        }
        item.setUnidadeProducao(up);

        Lote lote = loteRepository.findById(dto.loteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
        item.setLote(lote);

        if (dto.categoriaId() != null) {
            item.setCategoria(
                    categoriaRepository.findById(dto.categoriaId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"))
            );
        }
        if (dto.culturaId() != null) {
            item.setCultura(
                    culturaRepository.findById(dto.culturaId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultura não encontrada"))
            );
        }

        item.setUnidadeProducao(resolveUnidade(dto));

        if (dto.granjeiraAgricolaId() != null) {
            item.setGranjeiraAgricola(
                    granjeiraAgricolaRepository.findById(dto.granjeiraAgricolaId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Granjeira Agrícola não encontrada"))
            );
        }
        if (dto.areaComOutroUsoId() != null) {
            item.setAreaComOutroUso(
                    areaComOutroUsoRepository.findById(dto.areaComOutroUsoId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área com Outro Uso não encontrada"))
            );
        }
        if (dto.areasRestricoesId() != null) {
            item.setAreasRestricoes(
                    areasRestricoesRepository.findById(dto.areasRestricoesId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Áreas com Restrições não encontradas"))
            );
        }
        if (dto.categoriaAnimalId() != null) {
            item.setCategoriaAnimal(
                    categoriaAnimalRepository.findById(dto.categoriaAnimalId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria Animal não encontrada"))
            );
        }

        // Copia os escalares
        item.setFormaExploracao(dto.formaExploracao());
        item.setSequenciaProdutoVegetal(dto.sequenciaProdutoVegetal());
        item.setAreaColhida(dto.areaColhida());
        item.setAreaPlantada(dto.areaPlantada());
        item.setQuantidadeColhida(dto.quantidadeColhida());
        item.setAreaExploradaGranjeiraAgricola(dto.areaExploradaGranjeiraAgricola());
        item.setAreaUtilizada(dto.areaUtilizada());
        item.setAreaUtilizadaRestricao(dto.areaUtilizadaRestricao());
        item.setTipoPastagem(dto.tipoPastagem());
        item.setAreaPastagem(dto.areaPastagem());
        item.setQuantidadeAnimal(dto.quantidadeAnimal());
        item.setAreaAproveitavelNaoUtilizada(dto.areaAproveitavelNaoUtilizada());
        item.setIndicadorGeralDeRestricao(dto.indicadorGeralDeRestricao());
        item.setAreaGeralItem(dto.areaGeralItem());

        return item;
    }
}

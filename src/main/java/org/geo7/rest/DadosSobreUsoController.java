// src/main/java/org/geo7/rest/DadosSobreUsoController.java
package org.geo7.rest;

import jakarta.transaction.Transactional;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.dto.DadosSobreUsoDTO;
import org.geo7.dto.ItemDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/dados-sobre-uso")
public class DadosSobreUsoController {

    private final DadosSobreUsoRepository dadosSobreUsoRepository;
    private final LoteRepository loteRepository;

    // usados no mapItem:
    private final CategoriaRepository categoriaRepository;
    private final CulturaRepository culturaRepository;
    private final UnidadeProducaoRepository unidadeProducaoRepository;
    private final GranjeiraAgricolaRepository granjeiraAgricolaRepository;
    private final AreaComOutroUsoRepository areaComOutroUsoRepository;
    private final AreasRestricoesRepository areasRestricoesRepository;
    private final CategoriaAnimalRepository categoriaAnimalRepository;

    public DadosSobreUsoController(
            DadosSobreUsoRepository dadosSobreUsoRepository,
            LoteRepository loteRepository,
            CategoriaRepository categoriaRepository,
            CulturaRepository culturaRepository,
            UnidadeProducaoRepository unidadeProducaoRepository,
            GranjeiraAgricolaRepository granjeiraAgricolaRepository,
            AreaComOutroUsoRepository areaComOutroUsoRepository,
            AreasRestricoesRepository areasRestricoesRepository,
            CategoriaAnimalRepository categoriaAnimalRepository
    ) {
        this.dadosSobreUsoRepository = dadosSobreUsoRepository;
        this.loteRepository = loteRepository;
        this.categoriaRepository = categoriaRepository;
        this.culturaRepository = culturaRepository;
        this.unidadeProducaoRepository = unidadeProducaoRepository;
        this.granjeiraAgricolaRepository = granjeiraAgricolaRepository;
        this.areaComOutroUsoRepository = areaComOutroUsoRepository;
        this.areasRestricoesRepository = areasRestricoesRepository;
        this.categoriaAnimalRepository = categoriaAnimalRepository;
    }



    // GET por id
    @GetMapping("{id}")
    public ResponseEntity<DadosSobreUsoDTO> get(@PathVariable Long id) {
        return dadosSobreUsoRepository.findById(id)
                .map(DadosSobreUsoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET por lote
    @GetMapping("/por-lote/{loteId}")
    public ResponseEntity<DadosSobreUsoDTO> porLote(@PathVariable Long loteId) {
        return dadosSobreUsoRepository.findAll().stream()
                .filter(d -> d.getLote() != null && loteId.equals(d.getLote().getId()))
                .findFirst()
                .map(DadosSobreUsoDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<DadosSobreUsoDTO> create(@RequestBody DadosSobreUsoDTO dto) {
        var entidade = new DadosSobreUso();

        if (dto.loteId() != null) {
            Lote lote = loteRepository.findById(dto.loteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
            entidade.setLote(lote);
        }
        entidade.setAreaTotalIsolado(dto.areaTotalIsolado());
        entidade.setAreaTotalConsorcio(dto.areaTotalConsorcio());
        entidade.setAreaTotalRotacao(dto.areaTotalRotacao());

        entidade.setItems(new ArrayList<>());
        if (dto.items() != null) {
            for (ItemDTO it : dto.items()) {
                var item = it.toEntity();
                item.setId(null);                  // garante INSERT p/ novos
                item.setDadosSobreUso(entidade);
                if (entidade.getLote() != null) item.setLote(entidade.getLote());
                entidade.getItems().add(item);
            }
        }

        var salvo = dadosSobreUsoRepository.save(entidade);
        return ResponseEntity.ok(DadosSobreUsoDTO.fromEntity(salvo));
    }

    @PutMapping("{id}")
    @Transactional
    public ResponseEntity<DadosSobreUsoDTO> update(@PathVariable Long id,
                                                   @RequestBody DadosSobreUsoDTO dto) {
        var entidade = dadosSobreUsoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "DadosSobreUso não encontrado"));

        // pai
        if (dto.loteId() != null) {
            Lote lote = loteRepository.findById(dto.loteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
            entidade.setLote(lote);
        }
        entidade.setAreaTotalIsolado(dto.areaTotalIsolado());
        entidade.setAreaTotalConsorcio(dto.areaTotalConsorcio());
        entidade.setAreaTotalRotacao(dto.areaTotalRotacao());

        // filhos (NÃO troque a lista! limpe e add)
        var target = entidade.getItems();
        if (target == null) {
            target = new ArrayList<>();
            entidade.setItems(target);
        } else {
            target.clear();
        }

        if (dto.items() != null) {
            for (ItemDTO it : dto.items()) {
                Item item = it.toEntity();        // sua nova versão sem argumentos
                item.setDadosSobreUso(entidade);  // FK do pai
                if (entidade.getLote() != null) { // garantir lote_id no item
                    item.setLote(entidade.getLote());
                }
                target.add(item);
            }
        }

        var salvo = dadosSobreUsoRepository.save(entidade);
        return ResponseEntity.ok(DadosSobreUsoDTO.fromEntity(salvo));
    }

    // DELETE
    @DeleteMapping("{id}")
    @Transactional
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        return dadosSobreUsoRepository.findById(id)
                .map(e -> { dadosSobreUsoRepository.delete(e); return ResponseEntity.noContent().build(); })
                .orElse(ResponseEntity.notFound().build());
    }

    // ----------------- mapeamentos -----------------

    private DadosSobreUso mapDtoToEntity(DadosSobreUsoDTO dto) {
        DadosSobreUso entidade = (dto.id() != null)
                ? dadosSobreUsoRepository.findById(dto.id()).orElse(new DadosSobreUso())
                : new DadosSobreUso();

        // lote no pai (necessário para popular lote_id em cada item)
        if (dto.loteId() != null) {
            Lote lote = loteRepository.findById(dto.loteId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
            entidade.setLote(lote);
        }

        entidade.setAreaTotalIsolado(dto.areaTotalIsolado());
        entidade.setAreaTotalConsorcio(dto.areaTotalConsorcio());
        entidade.setAreaTotalRotacao(dto.areaTotalRotacao());

        // (re)monta itens
        List<Item> itens = new ArrayList<>();
        if (dto.items() != null) {
            for (ItemDTO it : dto.items()) {
                Item item = it.toEntity();          // <-- sem argumentos
                item.setDadosSobreUso(entidade);    // vincula ao pai
                if (entidade.getLote() != null) {
                    item.setLote(entidade.getLote()); // garante lote_id
                }
                itens.add(item);
            }
        }
        entidade.setItems(itens);
        return entidade;
    }

    private Item mapItem(ItemDTO dto, DadosSobreUso entityDadosSobreUso) {
        Categoria categoria = null;
        if (dto.categoriaId() != null) {
            categoria = categoriaRepository.findById(dto.categoriaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"));
        }

        Cultura cultura = null;
        if (dto.culturaId() != null) {
            cultura = culturaRepository.findById(dto.culturaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultura não encontrada"));
        }

        UnidadeProducao unidade = null;
        if (dto.codigoUnidadeProducao() != null && !dto.codigoUnidadeProducao().isBlank()) {
            unidade = unidadeProducaoRepository.findByCodigoUnidade(dto.codigoUnidadeProducao())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Unidade de Produção não encontrada"));
        }

        GranjeiraAgricola granjeira = null;
        if (dto.granjeiraAgricolaId() != null) {
            granjeira = granjeiraAgricolaRepository.findById(dto.granjeiraAgricolaId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Granjeira Agrícola não encontrada"));
        }

        AreaComOutroUso outroUso = null;
        if (dto.areaComOutroUsoId() != null) {
            outroUso = areaComOutroUsoRepository.findById(dto.areaComOutroUsoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área com Outro Uso não encontrada"));
        }

        AreasRestricoes restr = null;
        if (dto.areasRestricoesId() != null) {
            restr = areasRestricoesRepository.findById(dto.areasRestricoesId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área de Restrição não encontrada"));
        }

        CategoriaAnimal catAnimal = null;
        if (dto.categoriaAnimalId() != null) {
            catAnimal = categoriaAnimalRepository.findById(dto.categoriaAnimalId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria Animal não encontrada"));
        }

        Item item = dto.toEntity();
        item.setDadosSobreUso(entityDadosSobreUso);
        if(entityDadosSobreUso.getLote() != null) {
            item.setLote(entityDadosSobreUso.getLote());
        }

        // IMPORTANTES:
        item.setDadosSobreUso(entityDadosSobreUso);           // FK para dados_sobre_uso
        item.setLote(entityDadosSobreUso.getLote());          // FK para lote_id

        return item;
    }

    private static Double bd(Double v) {
        return v == null ? Double.valueOf(0) : Double.valueOf(v);
    }

    // se também quiser um "nz" para BigDecimal:
    private static BigDecimal nz(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

}

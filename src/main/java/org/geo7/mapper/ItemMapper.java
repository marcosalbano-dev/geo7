//package org.geo7.mapper;
//
//import org.geo7.dto.ItemDTO;
//import org.geo7.model.entity.Item;
//import org.geo7.model.entity.Lote;
//import org.geo7.model.entity.UnidadeProducao;
//import org.geo7.model.repository.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.server.ResponseStatusException;
//
//public class ItemMapper {
//
//    private final ItemRepository itemRepository;
//    private UnidadeProducaoRepository unidadeProducaoRepository;
//    private LoteRepository loteRepository;
//    private CategoriaRepository categoriaRepository;
//    private CulturaRepository culturaRepository;
//
//
//    public ItemMapper(ItemRepository itemRepository) {
//        this.itemRepository = itemRepository;
//    }
//
//    private UnidadeProducao resolveUnidade(ItemDTO dto) {
//        if (dto.unidadeProducaoId() != null) {
//            return unidadeProducaoRepository.findById(dto.unidadeProducaoId())
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unidade de Produção (ID) não encontrada"));
//        }
//        if (dto.codigoUnidadeProducao() != null && !dto.codigoUnidadeProducao().isBlank()) {
//            return unidadeProducaoRepository.findByCodigoUnidade(dto.codigoUnidadeProducao().trim())
//                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Código de Unidade de Produção inválido"));
//        }
//        return null; // opcional
//    }
//
//    private Item mapDtoToEntity(ItemDTO dto) {
//        Item e = new Item();
//        e.setId(dto.id());
//
//        Lote lote = loteRepository.findById(dto.loteId())
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
//        e.setLote(lote);
//
//        if (dto.categoriaId() != null) {
//            e.setCategoria(
//                    categoriaRepository.findById(dto.categoriaId())
//                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria não encontrada"))
//            );
//        }
//        if (dto.culturaId() != null) {
//            e.setCultura(
//                    culturaRepository.findById(dto.culturaId())
//                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cultura não encontrada"))
//            );
//        }
//
//        e.setCodigoUnidadeProducao(resolveUnidade(dto));
//
//        if (dto.granjeiraAgricolaId() != null) {
//            e.setGranjeiraAgricola(
//                    granjeiraAgricolaRepository.findById(dto.granjeiraAgricolaId())
//                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Granjeira Agrícola não encontrada"))
//            );
//        }
//        if (dto.areaComOutroUsoId() != null) {
//            e.setAreaComOutroUso(
//                    areaComOutroUsoRepository.findById(dto.areaComOutroUsoId())
//                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Área com Outro Uso não encontrada"))
//            );
//        }
//        if (dto.areasRestricoesId() != null) {
//            e.setAreasRestricoes(
//                    areasRestricoesRepository.findById(dto.areasRestricoesId())
//                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Áreas com Restrições não encontradas"))
//            );
//        }
//        if (dto.categoriaAnimalId() != null) {
//            e.setCategoriaAnimal(
//                    categoriaAnimalRepository.findById(dto.categoriaAnimalId())
//                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoria Animal não encontrada"))
//            );
//        }
//
//        // Copia os escalares
//        e.setFormaExploracao(dto.formaExploracao());
//        e.setSequenciaProdutoVegetal(dto.sequenciaProdutoVegetal());
//        e.setAreaColhida(dto.areaColhida());
//        e.setAreaPlantada(dto.areaPlantada());
//        e.setQuantidadeColhida(dto.quantidadeColhida());
//        e.setAreaExploradaGranjeiraAgricola(dto.areaExploradaGranjeiraAgricola());
//        e.setAreaUltilizada(dto.areaUltilizada());
//        e.setAreaUltilizadaRestricao(dto.areaUltilizadaRestricao());
//        e.setTipoPastagem(dto.tipoPastagem());
//        e.setAreaPastagem(dto.areaPastagem());
//        e.setQuantidadeAnimal(dto.quantidadeAnimal());
//        e.setAreaAproveitavelNaoUltilizada(dto.areaAproveitavelNaoUltilizada());
//        e.setIndicadorGeralDeRestricao(dto.indicadorGeralDeRestricao());
//        e.setAreaGeralItem(dto.areaGeralItem());
//
//        return e;
//    }
//}

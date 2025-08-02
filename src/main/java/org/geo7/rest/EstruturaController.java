package org.geo7.rest;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.geo7.model.entity.Estrutura;
import org.geo7.model.entity.FormaObtencao;
import org.geo7.model.entity.Lote;
import org.geo7.model.entity.SituacaoJuridica;
import org.geo7.model.repository.EstruturaRepository;
import org.geo7.model.repository.FormaObtencaoRepository;
import org.geo7.model.repository.LoteRepository;
import org.geo7.model.repository.SituacaoJuridicaRepository;
import org.geo7.rest.dto.EstruturaDTO;
import org.geo7.rest.dto.SituacaoJuridicaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/estrutura")
@CrossOrigin(origins = "http://localhost:4200")
public class EstruturaController {

    private final EstruturaRepository estruturaRepository;
    private final LoteRepository loteRepository;
    private final FormaObtencaoRepository formaObtencaoRepository;
    private final SituacaoJuridicaRepository situacaoJuridicaRepository;

    public EstruturaController(EstruturaRepository estruturaRepository,
                               LoteRepository loteRepository,
                               FormaObtencaoRepository formaObtencaoRepository,
                               SituacaoJuridicaRepository situacaoJuridicaRepository) {
        this.estruturaRepository = estruturaRepository;
        this.loteRepository = loteRepository;
        this.formaObtencaoRepository = formaObtencaoRepository;
        this.situacaoJuridicaRepository = situacaoJuridicaRepository;
    }

    @GetMapping
    public ResponseEntity<List<EstruturaDTO>> findAll() {
        List<EstruturaDTO> estruturas = estruturaRepository.findAll()
                .stream()
                .map(estrutura -> {
                    Lote lote = estrutura.getLote();
                    FormaObtencao forma = formaObtencaoRepository
                            .findFirstByLoteId(lote.getId())
                            .orElse(null);
                    return EstruturaDTO.fromEntity(estrutura, forma);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(estruturas);
    }

    @GetMapping("estruturas/por-lote/{loteId}")
    public ResponseEntity<EstruturaDTO> buscarPorLoteId(@PathVariable Long loteId) {
        Optional<Estrutura> estruturaOpt = estruturaRepository.findByLoteId(loteId);
        if (estruturaOpt.isEmpty()) return ResponseEntity.notFound().build();

        Estrutura estrutura = estruturaOpt.get();
        FormaObtencao forma = formaObtencaoRepository.findFirstByLoteId(loteId).orElse(null);
        return ResponseEntity.ok(EstruturaDTO.fromEntity(estrutura, forma));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstruturaDTO> findById(@PathVariable Long id) {
        return estruturaRepository.findById(id)
                .map(estrutura -> {
                    // Busque a forma vinculada ao lote (e situação jurídica, se desejar)
                    Lote lote = estrutura.getLote();
                    FormaObtencao forma = formaObtencaoRepository
                            .findFirstByLoteIdAndSituacaoJuridicaId(lote.getId(), lote.getSituacaoJuridica().getId())
                            .orElse(null);


                    return EstruturaDTO.fromEntity(estrutura, forma);
                })
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id));
    }

    @PostMapping
    public ResponseEntity<EstruturaDTO> salvarEstrutura(@Valid @RequestBody EstruturaDTO dto) {
//        Optional<Lote> loteOpt = loteRepository.findById(dto.loteId());
//        if (loteOpt.isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        Optional<SituacaoJuridica> situacaoOpt = situacaoJuridicaRepository.findById(dto.situacaoJuridicaId());
//        if (situacaoOpt.isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        Optional<FormaObtencao> formaOpt = formaObtencaoRepository.findById(dto.formaObtencaoId());
//        if(formaOpt.isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        Lote lote = loteOpt.get();
//        SituacaoJuridica situacao = situacaoOpt.get();
//        FormaObtencao formaObtencao = formaOpt.get();

        Lote lote = loteRepository.findById(dto.loteId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Lote não encontrado"));

        SituacaoJuridica situacao = situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Situação jurídica não encontrada"));

        FormaObtencao forma = new FormaObtencao();
        forma.setDescricaoFormaDeObtencao(dto.descricaoFormaDeObtencao());
        forma.setOficio(dto.oficio());
        forma.setMatricula(dto.matricula());
        forma.setLivro(dto.livro());
        forma.setNomeCartorio(dto.nomeCartorio());
        forma.setDataRegistro(dto.dataRegistro());
        forma.setNumeroRegistro(dto.numeroRegistro());
        forma.setMunicipioCartorio(dto.municipioCartorio());
        forma.setNumeroHerdeiros(dto.numeroHerdeiros());
        forma.setLote(lote);
        forma.setSituacaoJuridica(situacao);
        formaObtencaoRepository.save(forma);

        Estrutura estrutura = dto.toEntity(lote, situacao, forma);
        estruturaRepository.save(estrutura);

//        FormaObtencao forma = FormaObtencao.builder()
//                //.descricaoFormaDeObtencao(dto.formaObtencaoSelecionada())
//                .oficio(dto.oficio())
//                .matricula(dto.matricula())
//                .livro(dto.livro())
//                .nomeCartorio(dto.nomeCartorio())
//                .dataRegistro(String.valueOf(estrutura.getDhc())) // ou parse dto.dataRegistro se quiser mais exato
//                .numeroRegistro(dto.numeroRegistro())
//                .areaRegistrada(estrutura.getValorTotal()) // ou parse dto.areaRegistrada()
//                .areaMedida(estrutura.getValorTerraNua())  // ou parse dto.areaMedida()
//                .municipioCartorio(dto.municipioCartorio())
//                .dataPosse(estrutura.getDhc()) // ou parse dto.dataPosse()
//                .numeroHerdeiros(dto.numeroHerdeirosForma())
//                .lote(lote)
//                .situacaoJuridica(situacao)
//                .build();



        return ResponseEntity.created(URI.create("/api/estrutura/" + estrutura.getId()))
                .body(EstruturaDTO.fromEntity(estrutura, forma));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstruturaDTO> atualizar(@PathVariable Long id,
                                                  @Valid @RequestBody EstruturaDTO dto) {
        System.out.println("Recebido id: " + id);
        System.out.println("DTO.id: " + dto.id());
        System.out.println("DTO.loteId: " + dto.loteId());
        System.out.println("DTO.situacaoJuridicaId: " + dto.situacaoJuridicaId());
        System.out.println("DTO.municipioId: " + dto.municipioId());
        System.out.println("DTO.distritoId: " + dto.distritoId());
        System.out.println("DTO.descricaoFormaObtencao: " + dto.descricaoFormaDeObtencao());

        return estruturaRepository.findById(id)
                .map(existingEstrutura -> {
                    // Busca o lote e a situação jurídica
                    Lote lote = loteRepository.findById(dto.loteId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "Lote não encontrado com id: " + dto.loteId()));

                    SituacaoJuridica situacao = situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "Situação jurídica não encontrada com id: " + dto.situacaoJuridicaId()));

                    // Busca a forma vinculada ao lote
                    FormaObtencao forma = formaObtencaoRepository.findFirstByLoteId(lote.getId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "Forma de Obtenção não encontrada para lote: " + lote.getId()));

                    // Atualiza os campos da forma com dados do DTO
                    // (Adapte para todos os campos que deseja permitir atualizar!)
                    forma.setDescricaoFormaDeObtencao(dto.descricaoFormaDeObtencao());
                    forma.setOficio(dto.oficio());
                    forma.setMatricula(dto.matricula());
                    forma.setLivro(dto.livro());
                    forma.setNomeCartorio(dto.nomeCartorio());
                    forma.setDataRegistro(dto.dataRegistro());
                    forma.setNumeroRegistro(dto.numeroRegistro());
                    forma.setMunicipioCartorio(dto.municipioCartorio());
                    forma.setNumeroHerdeiros(dto.numeroHerdeirosForma());
                    // BigDecimal e Date
                    if (dto.areaRegistrada() != null && !dto.areaRegistrada().isBlank())
                        forma.setAreaRegistrada(new java.math.BigDecimal(dto.areaRegistrada()));
                    if (dto.areaMedida() != null && !dto.areaMedida().isBlank())
                        forma.setAreaMedida(new java.math.BigDecimal(dto.areaMedida()));
                    if (dto.dataPosse() != null && !dto.dataPosse().isBlank()) {
                        try {
                            forma.setDataPosse(new java.text.SimpleDateFormat("yyyy-MM-dd").parse(dto.dataPosse()));
                        } catch (Exception e) { /* trate ou ignore se preferir */ }
                    }
                    forma.setLote(lote);
                    forma.setSituacaoJuridica(situacao); // garanta o vínculo
                    formaObtencaoRepository.save(forma);

                    // Atualiza a estrutura
                    Estrutura updated = dto.toEntity(lote, situacao, forma);
                    updated.setId(id);
                    updated = estruturaRepository.save(updated);

                    return ResponseEntity.ok(EstruturaDTO.fromEntity(updated, forma));
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (estruturaRepository.existsById(id)) {
            estruturaRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id);
    }

    @GetMapping("/por-lote/{loteId}")
    public ResponseEntity<List<EstruturaDTO>> findByLoteId(@PathVariable Long loteId) {
        Optional<Estrutura> estruturas = estruturaRepository.findByLoteId(loteId);

        // Busca a forma de obtenção vinculada ao lote (pode ser null)
        Optional<FormaObtencao> formaOpt = formaObtencaoRepository.findFirstByLoteId(loteId);

        List<EstruturaDTO> dtos = estruturas.stream()
                .map(estrutura -> EstruturaDTO.fromEntity(estrutura, formaOpt.orElse(null)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    public Estrutura salvarEstruturaComSituacao(EstruturaDTO estruturaDTO, SituacaoJuridicaDTO situacaoDTO) {
        SituacaoJuridica situacao = new SituacaoJuridica();
        situacao.setNome(situacaoDTO.situacaoSelecionada());

        Lote lote = loteRepository.findById(estruturaDTO.loteId())
                .orElseThrow(() -> new EntityNotFoundException("Lote não encontrado"));

        situacao.getLotes().add(lote);
        situacao = situacaoJuridicaRepository.save(situacao);

        FormaObtencao forma = FormaObtencao.builder()
                .descricaoFormaDeObtencao(situacaoDTO.formaObtencaoSelecionada())
                .dataPosse(situacaoDTO.dataPosse())
                .areaMedida(situacaoDTO.areaPosse())
                .livro(situacaoDTO.livro())
                .areaRegistrada(situacaoDTO.areaRegistrada())
                .nomeCartorio(situacaoDTO.nomeCartorio())
                .municipioCartorio(situacaoDTO.municipioCartorio())
                .dataRegistro(situacaoDTO.dataRegistro())
                .oficio(situacaoDTO.oficio())
                .matricula(situacaoDTO.matricula())
                .numeroRegistro(situacaoDTO.numeroRegistro())
                .lote(lote)
                .situacaoJuridica(situacao)
                .build();

        formaObtencaoRepository.save(forma);

        Estrutura estrutura = estruturaDTO.toEntity(lote, situacao, forma);
        estrutura = estruturaRepository.save(estrutura);

        return estrutura;
    }
}

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
                .map(EstruturaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estruturas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EstruturaDTO> findById(@PathVariable Long id) {
        return estruturaRepository.findById(id)
                .map(EstruturaDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id));
    }

    @PostMapping
    public ResponseEntity<EstruturaDTO> salvarEstrutura(@Valid @RequestBody EstruturaDTO dto) {
        Optional<Lote> loteOpt = loteRepository.findById(dto.loteId());
        if (loteOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Optional<SituacaoJuridica> situacaoOpt = situacaoJuridicaRepository.findByNome(dto.situacaoSelecionada());
        if (situacaoOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Lote lote = loteOpt.get();
        SituacaoJuridica situacao = situacaoOpt.get();

        Estrutura estrutura = dto.toEntity(lote, situacao);
        estruturaRepository.save(estrutura);

        FormaObtencao forma = FormaObtencao.builder()
                .descricaoFormaDeObtencao(dto.formaObtencaoSelecionada())
                .oficio(dto.oficio())
                .matricula(dto.matricula())
                .livro(dto.livro())
                .nomeCartorio(dto.nomeCartorio())
                .dataRegistro(String.valueOf(estrutura.getDhc())) // ou parse dto.dataRegistro se quiser mais exato
                .numeroRegistro(dto.numeroRegistro())
                .areaRegistrada(estrutura.getValorTotal()) // ou parse dto.areaRegistrada()
                .areaMedida(estrutura.getValorTerraNua())  // ou parse dto.areaMedida()
                .municipioCartorio(dto.municipioCartorio())
                .dataPosse(estrutura.getDhc()) // ou parse dto.dataPosse()
                .numeroHerdeiros(dto.numeroHerdeirosForma())
                .lote(lote)
                .situacaoJuridica(situacao)
                .build();

        formaObtencaoRepository.save(forma);

        return ResponseEntity.created(URI.create("/api/estrutura/" + estrutura.getId()))
                .body(EstruturaDTO.fromEntity(estrutura));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstruturaDTO> update(@PathVariable Long id,
                                               @Valid @RequestBody EstruturaDTO dto) {
        return estruturaRepository.findById(id)
                .map(existingEstrutura -> {
                    Lote lote = loteRepository.findById(dto.loteId())
                            .orElseThrow(() -> new ResponseStatusException(
                                    HttpStatus.BAD_REQUEST, "Lote não encontrado com id: " + dto.loteId()));

                    String tipo = dto.situacaoSelecionada();
                    SituacaoJuridica situacao = situacaoJuridicaRepository.findByNome(tipo)
                            .orElseGet(() -> {
                                SituacaoJuridica nova = new SituacaoJuridica();
                                nova.setNome(tipo);
                                return situacaoJuridicaRepository.save(nova);
                            });

                    Estrutura updated = dto.toEntity(lote, situacao);
                    updated.setId(id);
                    updated = estruturaRepository.save(updated);

                    for (FormaObtencao forma : lote.getFormaObtencao()) {
                        formaObtencaoRepository.save(forma);
                    }

                    return ResponseEntity.ok(EstruturaDTO.fromEntity(updated));
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
        List<EstruturaDTO> estruturas = estruturaRepository.findByLoteId(loteId)
                .stream()
                .map(EstruturaDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(estruturas);
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

        Estrutura estrutura = estruturaDTO.toEntity(lote, situacao);
        estrutura = estruturaRepository.save(estrutura);

        return estrutura;
    }
}

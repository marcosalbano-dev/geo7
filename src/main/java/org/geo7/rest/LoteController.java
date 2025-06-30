package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.rest.dto.LoteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/lotes")
@CrossOrigin(origins = "http://localhost:4200")
public class LoteController {

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private MunicipioRepository municipioRepository;

    @Autowired
    private SituacaoJuridicaRepository situacaoJuridicaRepository;

    @Autowired
    private FormaObtencaoRepository formaObtencaoRepository;

    @Autowired
    private DistritoRepository distritoRepository;

    public LoteController(LoteRepository loteRepository, FormaObtencaoRepository formaObtencaoRepository) {
        this.loteRepository = loteRepository;
        this.formaObtencaoRepository = formaObtencaoRepository;
    }


    @PostMapping
    public ResponseEntity<LoteDTO> salvarEstrutura(@Valid @RequestBody LoteDTO dto) {
        Optional<Municipio> municipioOpt = municipioRepository.findById(dto.municipioId());
        System.out.println("SituacaoJuridicaId recebido: " + dto.situacaoJuridicaId());
        Optional<SituacaoJuridica> situacaoOpt = situacaoJuridicaRepository.findById(dto.situacaoJuridicaId());

        if (municipioOpt.isEmpty() || situacaoOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        Municipio municipio = municipioOpt.get();
        SituacaoJuridica situacao = situacaoOpt.get();
        Optional<Distrito> distritoOpt = distritoRepository.findById(dto.distritoId());
        if (distritoOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Distrito distrito = distritoOpt.get();

        Lote lote = dto.toEntity(municipio, situacao, distrito);

        loteRepository.save(lote);

        if (dto.formaObtencaoSelecionada() != null && !dto.formaObtencaoSelecionada().isBlank()) {
            FormaObtencao forma = FormaObtencao.builder()
                    .descricaoFormaDeObtencao(dto.formaObtencaoSelecionada())
                    .dataRegistro(String.valueOf(new Date()))
                    .lote(lote)
                    .situacaoJuridica(situacao)
                    .build();

            formaObtencaoRepository.save(forma);
        }

        return ResponseEntity.created(URI.create("/api/lotes/" + lote.getId()))
                .body(LoteDTO.fromEntity(lote));
    }

    @GetMapping("{id}")
    public ResponseEntity<LoteDTO> buscarPorId(@PathVariable Long id) {
        return loteRepository.findById(id)
                .map(LoteDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }

    @GetMapping("{proprietario}")
    public ResponseEntity<LoteDTO> buscarPorProprietario(@PathVariable String proprietario) {
        return loteRepository.findByProprietario(proprietario)
                .map(LoteDTO::fromEntity)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Proprietário não encontrado"));
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<LoteDTO>> listarTodos() {
        List<LoteDTO> lotes = loteRepository.findAllWithFormaObtencao()
                .stream()
                .map(LoteDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(lotes);
    }

//    @PutMapping("{id}")
//    public ResponseEntity<LoteDTO> atualizar(@PathVariable Long id, @RequestBody @Valid LoteDTO dto) {
//        return loteRepository.findById(id)
//                .map(loteExistente -> {
//                    Municipio municipio = municipioRepository.findById(dto.municipioId())
//                            .orElseThrow(() -> new RuntimeException("Município não encontrado"));
//
//                    SituacaoJuridica sj = dto.situacaoJuridicaId() != null
//                            ? situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
//                            .orElseThrow(() -> new RuntimeException("Situação jurídica não encontrada"))
//                            : null;
//
//                    Lote atualizado = dto.toEntity(municipio, sj);
//                    atualizado.setId(id); // garante atualização
//
//                    loteRepository.save(atualizado);
//
//                    if (dto.formaObtencaoSelecionada() != null && !dto.formaObtencaoSelecionada().isBlank()) {
//                        FormaObtencao forma = FormaObtencao.builder()
//                                .descricaoFormaDeObtencao(dto.formaObtencaoSelecionada())
//                                .dataRegistro(String.valueOf(new Date()))
//                                .lote(atualizado)
//                                .situacaoJuridica(sj)
//                                .build();
//
//                        formaObtencaoRepository.save(forma);
//                    }
//
//                    return ResponseEntity.ok(LoteDTO.fromEntity(atualizado));
//                })
//                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
//    }

    @DeleteMapping("{id}")
    public ResponseEntity<Object> deletar(@PathVariable Long id) {
        return loteRepository.findById(id)
                .map(l -> {
                    loteRepository.delete(l);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }
}

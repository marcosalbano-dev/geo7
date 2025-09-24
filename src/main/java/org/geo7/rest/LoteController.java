package org.geo7.rest;

import jakarta.validation.Valid;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.dto.LoteDTO;
import org.geo7.dto.LoteFiltroDTO;
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

    public LoteController(
            LoteRepository loteRepository,
            FormaObtencaoRepository formaObtencaoRepository
    ) {
        this.loteRepository = loteRepository;
        this.formaObtencaoRepository = formaObtencaoRepository;
    }

    @PostMapping("/filtrar")
    public List<LoteDTO> filtrar(@RequestBody LoteFiltroDTO filtro) {
        return loteRepository.filtrarLotes(filtro)
                .stream()
                .map(LoteDTO::fromEntity)
                .toList();
    }

    @PostMapping
    public ResponseEntity<LoteDTO> salvarLote(@Valid @RequestBody LoteDTO dto) {
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

    @GetMapping("proprietario/{proprietario}")
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

    @PutMapping("{id}")
    public ResponseEntity<LoteDTO> atualizar(@PathVariable Long id, @RequestBody @Valid LoteDTO dto) {
        return loteRepository.findById(id)
                .map(loteExistente -> {
                    // Relacionamentos
                    Municipio municipio = municipioRepository.findById(dto.municipioId())
                            .orElseThrow(() -> new RuntimeException("Município não encontrado"));

                    SituacaoJuridica sj = dto.situacaoJuridicaId() != null
                            ? situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                            .orElseThrow(() -> new RuntimeException("Situação jurídica não encontrada"))
                            : null;

                    Distrito distrito = dto.distritoId() != null
                            ? distritoRepository.findById(dto.distritoId())
                            .orElseThrow(() -> new RuntimeException("Distrito não encontrado"))
                            : null;

                    // Atualiza os campos
                    loteExistente.setProprietario(dto.proprietario());
                    loteExistente.setArea(dto.area());
                    loteExistente.setDenominacaoImovel(dto.denominacaoImovel());
                    loteExistente.setNumero(dto.numero());
                    loteExistente.setDhc(dto.dhc() != null ? dto.dhc() : new Date());
                    loteExistente.setDhm(dto.dhm() != null ? dto.dhm() : new Date());
                    loteExistente.setPerimetro(dto.perimetro());
                    loteExistente.setSncr(dto.sncr());
                    loteExistente.setCpf(dto.cpf());
                    loteExistente.setMunicipio(municipio);
                    loteExistente.setSituacaoJuridica(sj);
                    loteExistente.setDistrito(distrito);
                    loteExistente.setDataTerminoPeriodoDeUso(dto.dataTerminoPeriodoDeUso());

                    // Atualize outros campos se houver
                    // Atualizar forma de obtenção (troca tudo, deixa só o selecionado)
                    if (dto.formaObtencaoSelecionada() != null && !dto.formaObtencaoSelecionada().isBlank()) {
                        // Limpa todas as formas de obtenção
                        loteExistente.getFormaObtencao().clear();

                        // Cria a nova forma de obtenção
                        FormaObtencao novaForma = FormaObtencao.builder()
                                .descricaoFormaDeObtencao(dto.formaObtencaoSelecionada())
                                .dataRegistro(String.valueOf(new Date()))
                                .lote(loteExistente)
                                .situacaoJuridica(sj)
                                .build();

                        // Adiciona ao lote
                        loteExistente.getFormaObtencao().add(novaForma);
                    } else {
                        // Se o campo vier vazio, pode limpar tudo (opcional)
                        loteExistente.getFormaObtencao().clear();
                    }

                    loteRepository.save(loteExistente);

                    return ResponseEntity.ok(LoteDTO.fromEntity(loteExistente));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Lote não encontrado"));
    }


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

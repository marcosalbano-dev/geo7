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
import org.geo7.dto.EstruturaDTO;
import org.geo7.dto.SituacaoJuridicaDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.Date;
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
        if (estruturaRepository.findByLoteId(dto.loteId()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe estrutura para o lote " + dto.loteId());
        }
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

        return ResponseEntity.created(URI.create("/api/estrutura/" + estrutura.getId()))
                .body(EstruturaDTO.fromEntity(estrutura, forma));
    }

    @PutMapping("/por-lote/{loteId}")
    public ResponseEntity<EstruturaDTO> upsertPorLote(@PathVariable Long loteId,
                                                      @RequestBody @Valid EstruturaDTO dto) {
        Estrutura e = estruturaRepository.findByLoteId(loteId)
                .orElseGet(() -> { var novo = new Estrutura(); novo.setLote(loteRepository.getReferenceById(loteId)); return novo; });

        // aplique campos do DTO em 'e' (applyDtoToEstrutura)
        applyDtoToEstrutura(dto, e);
        e.setDhm(new java.util.Date());
        e = estruturaRepository.save(e);

        var forma = formaObtencaoRepository.findFirstByLoteId(loteId).orElse(null);
        return ResponseEntity.ok(EstruturaDTO.fromEntity(e, forma));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EstruturaDTO> atualizar(@PathVariable Long id,
                                                  @Valid @RequestBody EstruturaDTO dto) {

        Estrutura existente = estruturaRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Estrutura não encontrada com id: " + id));

        // 1) Lote e Situação Jurídica obrigatórios
        Lote lote = loteRepository.findById(dto.loteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Lote não encontrado com id: " + dto.loteId()));

        SituacaoJuridica situacao = situacaoJuridicaRepository.findById(dto.situacaoJuridicaId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Situação jurídica não encontrada com id: " + dto.situacaoJuridicaId()));

        // 2) Forma de Obtenção do lote - UPSERT
        FormaObtencao forma = formaObtencaoRepository.findFirstByLoteId(lote.getId())
                .orElseGet(() -> {
                    FormaObtencao f = new FormaObtencao();
                    f.setLote(lote);
                    return f;
                });

        forma.setSituacaoJuridica(situacao);
        forma.setDescricaoFormaDeObtencao(dto.descricaoFormaDeObtencao());
        forma.setOficio(dto.oficio());
        forma.setMatricula(dto.matricula());
        forma.setLivro(dto.livro());
        forma.setNomeCartorio(dto.nomeCartorio());
        // data_registro é VARCHAR na tabela -> persista como veio
        forma.setDataRegistro(emptyToNull(dto.dataRegistro()));
        forma.setNumeroRegistro(dto.numeroRegistro());
        forma.setMunicipioCartorio(dto.municipioCartorio());
        forma.setNumeroHerdeiros(dto.numeroHerdeirosForma());

        // data_posse é DATE na tabela
        forma.setDataPosse(parseUtilDate(dto.dataPosse()));

        // BigDecimals (aceitando vírgula)
        forma.setAreaRegistrada(parseBigDecimal(dto.areaRegistrada()));
        forma.setAreaMedida(parseBigDecimal(dto.areaMedida()));

        forma = formaObtencaoRepository.save(forma);

        // 3) Atualizar a Estrutura existente (copiando campo a campo)
        existente.setLote(lote);
        existente.setFamiliasResidentes(dto.familiasResidentes());
        existente.setPessoasResidentes(dto.pessoasResidentes());
        existente.setTrabalhadoresComCarteira(dto.trabalhadoresComCarteira());
        existente.setTrabalhadoresSemCarteira(dto.trabalhadoresSemCarteira());
        existente.setMaoDeObraFamiliar(dto.maoDeObraFamiliar());
        existente.setValorTotal(dto.valorTotal());
        existente.setValorDasBenfeitorias(dto.valorDasBenfeitorias());
        existente.setValorOutrasAtividades(dto.valorOutrasAtividades());
        existente.setValorTerraNua(dto.valorTerraNua());
        existente.setAreaIrrigada(dto.areaIrrigada());
        existente.setLitigio(dto.litigio());
        existente.setEntregouMemorialPlanilha(Boolean.TRUE.equals(dto.entregouMemorialPlanilha()));
        existente.setDestinacaoDoImovel(dto.destinacaoDoImovel());
        existente.setPontoDeReferencia(dto.pontoDeReferencia());
        existente.setNumeroHerdeiros(dto.numeroHerdeiros());
        existente.setPorcentagemDetencao(dto.porcentagemDetencao());
        existente.setObsLitigio(dto.obsLitigio());
        existente.setTipoEnergiaEletrica(dto.tipoEnergiaEletrica());
        existente.setIsPossuiElergiaEletrica(Boolean.TRUE.equals(dto.isPossuiElergiaEletrica()));
        existente.setIsPossuiEnergiaAlternativa(Boolean.TRUE.equals(dto.isPossuiEnergiaAlternativa()));
        existente.setIsRedeDeAbastecimento(Boolean.TRUE.equals(dto.isRedeDeAbastecimento()));
        existente.setIsIrrigacao(Boolean.TRUE.equals(dto.isIrrigacao()));
        existente.setIsFonteAguaExterna(Boolean.TRUE.equals(dto.isFonteAguaExterna()));
        existente.setIsRioOuRiacho(Boolean.TRUE.equals(dto.isRioOuRiacho()));
        existente.setIsRioOuRiachoPerene(Boolean.TRUE.equals(dto.isRioOuRiachoPerene()));
        existente.setIsAcude(Boolean.TRUE.equals(dto.isAcude()));
        existente.setIsAcudePerene(Boolean.TRUE.equals(dto.isAcudePerene()));
        existente.setIsOlhoDagua(Boolean.TRUE.equals(dto.isOlhoDagua()));
        existente.setIsOlhoDaguaPerene(Boolean.TRUE.equals(dto.isOlhoDaguaPerene()));
        existente.setIsLagoa(Boolean.TRUE.equals(dto.isLagoa()));
        existente.setIsLagoaPerene(Boolean.TRUE.equals(dto.isLagoaPerene()));
        existente.setIsPoco(Boolean.TRUE.equals(dto.isPoco()));
        existente.setIsPocoPerene(Boolean.TRUE.equals(dto.isPocoPerene()));
        existente.setUsoDaguaAcude(dto.usoDaguaAcude());
        existente.setUsoDaguaLagoa(dto.usoDaguaLagoa());
        existente.setUsoDaguaOlhoDagua(dto.usoDaguaOlhoDagua());
        existente.setUsoDaguaPoco(dto.usoDaguaPoco());
        existente.setUsoDaguaRioOuRiacho(dto.usoDaguaRioOuRiacho());

        existente = estruturaRepository.save(existente);

        // 4) Devolve DTO completo já com a forma persistida
        return ResponseEntity.ok(EstruturaDTO.fromEntity(existente, forma));
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
    public ResponseEntity<EstruturaDTO> findByLoteId(@PathVariable Long loteId) {
        Estrutura e = estruturaRepository.findByLoteId(loteId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Nenhuma estrutura para o lote " + loteId));

        FormaObtencao forma = formaObtencaoRepository.findFirstByLoteId(loteId).orElse(null);
        return ResponseEntity.ok(EstruturaDTO.fromEntity(e, forma));
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

    // ---------- Helpers ----------

    // --- helpers ---

    private static String emptyToNull(String s) {
        return (s == null || s.isBlank() || "null".equalsIgnoreCase(s)) ? null : s;
    }

    private static java.math.BigDecimal parseBigDecimal(String v) {
        if (v == null || v.isBlank() || "null".equalsIgnoreCase(v)) return null;
        return new java.math.BigDecimal(v.replace(",", "."));
    }

    private static java.math.BigDecimal parseDecimal(String v) {
        if (v == null) return null;
        String s = v.trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
        s = s.replace(",", "."); // permite vírgula
        return new java.math.BigDecimal(s);
    }

    // helper: retorna java.util.Date (na prática java.sql.Date, que herda de java.util.Date)
    private static java.util.Date parseUtilDate(String yyyyMMdd) {
        if (yyyyMMdd == null || yyyyMMdd.isBlank() || "null".equalsIgnoreCase(yyyyMMdd)) return null;
        try {
            return java.sql.Date.valueOf(yyyyMMdd); // espera "yyyy-MM-dd"
        } catch (Exception e) {
            return null;
        }
    }

    /** Copia somente campos atualizáveis do DTO para a entidade existente. */
    private static void applyDtoToEstrutura(EstruturaDTO dto, Estrutura e) {
        e.setFamiliasResidentes(dto.familiasResidentes());
        e.setPessoasResidentes(dto.pessoasResidentes());
        e.setTrabalhadoresComCarteira(dto.trabalhadoresComCarteira());
        e.setTrabalhadoresSemCarteira(dto.trabalhadoresSemCarteira());
        e.setMaoDeObraFamiliar(dto.maoDeObraFamiliar());

        e.setValorTotal(               dto.valorTotal());
        e.setValorDasBenfeitorias(     dto.valorDasBenfeitorias());
        e.setValorOutrasAtividades(    dto.valorOutrasAtividades());
        e.setValorTerraNua(            dto.valorTerraNua());
        e.setAreaIrrigada(             dto.areaIrrigada());

        e.setLitigio(dto.litigio());
        e.setEntregouMemorialPlanilha(Boolean.TRUE.equals(dto.entregouMemorialPlanilha()));
        e.setDestinacaoDoImovel(dto.destinacaoDoImovel());
        e.setPontoDeReferencia(dto.pontoDeReferencia());
        e.setNumeroHerdeiros(dto.numeroHerdeiros());
        e.setPorcentagemDetencao(dto.porcentagemDetencao());
        e.setObsLitigio(dto.obsLitigio());
        e.setTipoEnergiaEletrica(dto.tipoEnergiaEletrica());

        e.setIsPossuiElergiaEletrica(Boolean.TRUE.equals(dto.isPossuiElergiaEletrica()));
        e.setIsPossuiEnergiaAlternativa(Boolean.TRUE.equals(dto.isPossuiEnergiaAlternativa()));
        e.setIsRedeDeAbastecimento(Boolean.TRUE.equals(dto.isRedeDeAbastecimento()));
        e.setIsIrrigacao(Boolean.TRUE.equals(dto.isIrrigacao()));
        e.setIsFonteAguaExterna(Boolean.TRUE.equals(dto.isFonteAguaExterna()));

        e.setIsRioOuRiacho(Boolean.TRUE.equals(dto.isRioOuRiacho()));
        e.setIsRioOuRiachoPerene(Boolean.TRUE.equals(dto.isRioOuRiachoPerene()));
        e.setIsAcude(Boolean.TRUE.equals(dto.isAcude()));
        e.setIsAcudePerene(Boolean.TRUE.equals(dto.isAcudePerene()));
        e.setIsOlhoDagua(Boolean.TRUE.equals(dto.isOlhoDagua()));
        e.setIsOlhoDaguaPerene(Boolean.TRUE.equals(dto.isOlhoDaguaPerene()));
        e.setIsLagoa(Boolean.TRUE.equals(dto.isLagoa()));
        e.setIsLagoaPerene(Boolean.TRUE.equals(dto.isLagoaPerene()));
        e.setIsPoco(Boolean.TRUE.equals(dto.isPoco()));
        e.setIsPocoPerene(Boolean.TRUE.equals(dto.isPocoPerene()));

        e.setUsoDaguaAcude(dto.usoDaguaAcude());
        e.setUsoDaguaLagoa(dto.usoDaguaLagoa());
        e.setUsoDaguaOlhoDagua(dto.usoDaguaOlhoDagua());
        e.setUsoDaguaPoco(dto.usoDaguaPoco());
        e.setUsoDaguaRioOuRiacho(dto.usoDaguaRioOuRiacho());
    }

    private static String normalizeDateStr(String v) {
        if (v == null) return null;
        String s = v.trim();
        if (s.isEmpty() || "null".equalsIgnoreCase(s)) return null;
        try {
            var d = new java.text.SimpleDateFormat("yyyy-MM-dd").parse(s);
            return new java.text.SimpleDateFormat("yyyy-MM-dd").format(d);
        } catch (Exception e) {
            return s; // se veio "10/05/2024" ou "Wed Aug ..." e você não quiser rejeitar, mantenha
        }
    }
}


package org.geo7.service;

import lombok.RequiredArgsConstructor;
import org.geo7.dto.*;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.model.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExportacaoDpService {

    private final MunicipioRepository municipioRepo;
    private final LoteRepository loteRepo;
    private final EnderecoLoteRepository enderecoLoteRepo;
    private final EstruturaRepository estruturaRepo;
    private final FormaObtencaoRepository formaObtencaoRepo;
    private final DadosSobreUsoRepository dadosSobreUsoRepo;
    private final ItemRepository itemRepo;
    private final PessoaLoteRepository pessoaLoteRepo;
    private final PessoaRepository pessoaRepo;
    private final EnderecoPessoaRepository enderecoPessoaRepo;
    private final DocumentoPessoaRepository documentoPessoaRepo;

    @Transactional(readOnly = true)
    public Geo7MunicipioExportDTO carregarMunicipioAgregado(Long municipioId) {
        Municipio municipio = municipioRepo.findById(municipioId)
                .orElseThrow(() -> new NoSuchElementException("Município não encontrado: " + municipioId));

        // 1) Lotes do município
        List<Lote> lotes = loteRepo.findByMunicipioId(municipioId);

        if (lotes.isEmpty()) {
            return new Geo7MunicipioExportDTO(MunicipioDTO.fromEntity(municipio), List.of());
        }

        var loteIds = lotes.stream().map(Lote::getId).collect(Collectors.toSet());

        // 2) Endereços de lote, Estrutura e Formas de obtenção
        Map<Long, EnderecoLote> endPorLote = enderecoLoteRepo.findByLoteIdIn(loteIds).stream()
                .collect(Collectors.toMap(el -> el.getLote().getId(), el -> el, (a, b) -> a));

        Map<Long, Estrutura> estPorLote = estruturaRepo.findByLoteIdIn(loteIds).stream()
                .collect(Collectors.toMap(e -> e.getLote().getId(), e -> e, (a, b) -> a));

        Map<Long, List<FormaObtencao>> formasPorLote = formaObtencaoRepo.findByLoteIdIn(loteIds).stream()
                .collect(Collectors.groupingBy(f -> f.getLote().getId()));

        // 3) Uso (DadosSobreUso + Itens)
        Map<Long, DadosSobreUso> dsuPorLote = dadosSobreUsoRepo.findByLoteIdIn(loteIds).stream()
                .collect(Collectors.toMap(d -> d.getLote().getId(), d -> d, (a, b) -> a));

        var dsuIds = dsuPorLote.values().stream().map(DadosSobreUso::getId).collect(Collectors.toSet());
        Map<Long, List<Item>> itensPorDsu = dsuIds.isEmpty() ? Map.of()
                : itemRepo.findByDadosSobreUsoIdIn(dsuIds).stream().collect(Collectors.groupingBy(i -> i.getDadosSobreUso().getId()));

        // 4) Pessoas por lote
        Map<Long, List<PessoaLote>> pessoaLotePorLote = pessoaLoteRepo.findByLoteIdIn(loteIds).stream()
                .collect(Collectors.groupingBy(pl -> pl.getLote().getId()));

        var pessoaIds = pessoaLotePorLote.values().stream()
                .flatMap(List::stream).map(pl -> pl.getPessoa().getId())
                .collect(Collectors.toSet());

        Map<Long, Pessoa> pessoaPorId = pessoaIds.isEmpty() ? Map.of()
                : pessoaRepo.findAllById(pessoaIds).stream()
                .collect(Collectors.toMap(Pessoa::getId, p -> p));

        Map<Long, EnderecoPessoa> endPessoaPorPessoa = pessoaIds.isEmpty() ? Map.of()
                : enderecoPessoaRepo.findByPessoaIdIn(pessoaIds).stream()
                .collect(Collectors.toMap(ep -> ep.getPessoa().getId(), ep -> ep, (a,b)->a));

        Map<Long, DocumentoPessoa> docPorPessoa = pessoaIds.isEmpty() ? Map.of()
                : documentoPessoaRepo.findByPessoaIdIn(pessoaIds).stream()
                .collect(Collectors.toMap(dp -> dp.getPessoa().getId(), dp -> dp, (a,b)->a));

        // 5) Monta os agregados
        List<Geo7LoteAgregado> agregados = new ArrayList<>();
        for (Lote lote : lotes) {
            var endLote = Optional.ofNullable(endPorLote.get(lote.getId())).map(EnderecoLoteDTO::fromEntity).orElse(null);

            var estrutura = Optional.ofNullable(estPorLote.get(lote.getId()))
                    .map(e -> {
                        // opcionalmente, pegue a "forma" principal pra preencher os campos agregados do DTO de Estrutura
                        var formas = formasPorLote.getOrDefault(lote.getId(), List.of());
                        FormaObtencao forma = formas.isEmpty() ? null : formas.get(0);
                        return EstruturaDTO.fromEntity(e, forma);
                    }).orElse(null);

            var formas = formasPorLote.getOrDefault(lote.getId(), List.of())
                    .stream().map(FormaObtencaoDTO::fromEntity).toList();

            DadosSobreUsoDTO dsuDto = null;
            var dsu = dsuPorLote.get(lote.getId());
            if (dsu != null) {
                // Busca null-safe e garante coleção MODIFICÁVEL
                var itensEntity = new java.util.ArrayList<>(
                        itensPorDsu.getOrDefault(dsu.getId(), java.util.Collections.<Item>emptyList())
                );

                // vínculo bidirecional
                itensEntity.forEach(it -> it.setDadosSobreUso(dsu));

                // setter espera List<Item>
                dsu.setItems(itensEntity);

                dsuDto = DadosSobreUsoDTO.fromEntity(dsu);
            }

            // Pessoas do lote
            List<Geo7PessoaAgregada> pessoasAgregadas = pessoaLotePorLote.getOrDefault(lote.getId(), List.of())
                    .stream().map(pl -> {
                        var pessoa = pessoaPorId.get(pl.getPessoa().getId());
                        var ePessoa = endPessoaPorPessoa.get(pl.getPessoa().getId());
                        var dPessoa = docPorPessoa.get(pl.getPessoa().getId());
                        return new Geo7PessoaAgregada(
                                PessoaDTO.fromEntity(pessoa),
                                PessoaLoteDTO.fromEntity(pl),
                                ePessoa != null ? EnderecoPessoaDTO.fromEntity(ePessoa) : null,
                                dPessoa != null ? DocumentoPessoaDTO.fromEntity(dPessoa) : null
                        );
                    }).toList();

            agregados.add(new Geo7LoteAgregado(
                    LoteDTO.fromEntity(lote),
                    endLote,
                    estrutura,
                    formas,
                    dsuDto,
                    pessoasAgregadas
            ));
        }

        return new Geo7MunicipioExportDTO(MunicipioDTO.fromEntity(municipio), agregados);
    }


    public String gerarXml(Geo7MunicipioExportDTO dto) {
        return ExportacaoDpXmlWriter.buildMunicipioXml(dto);
    }
}


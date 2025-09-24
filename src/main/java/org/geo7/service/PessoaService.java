package org.geo7.service;

import lombok.RequiredArgsConstructor;
import org.geo7.dto.*;
import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PessoaService {

    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private PessoaLoteRepository pessoaLoteRepository;
    @Autowired private EnderecoPessoaRepository enderecoPessoaRepository;
    @Autowired private DocumentoPessoaRepository documentoPessoaRepository;
    //@Autowired private ProgramaGovernoRepository programaGovernoRepository;
    @Autowired private PronafRepository pronafRepository;
    @Autowired private MunicipioRepository municipioRepository;
    @Autowired private LoteRepository loteRepository;

    private static BigDecimal nz(BigDecimal v) { return v != null ? v : BigDecimal.ZERO; }

    public Optional<EditarDetentorResponseDTO> getEditarDetentorData(Long pessoaLoteId) {
        Optional<PessoaLote> pessoaLoteOpt = pessoaLoteRepository.findById(pessoaLoteId);
        if (pessoaLoteOpt.isEmpty()) return Optional.empty();

        PessoaLote pl = pessoaLoteOpt.get();
        Pessoa pessoa = pl.getPessoa();
        DocumentoPessoa documento = documentoPessoaRepository.findByPessoa(pessoa);
        EnderecoPessoa endereco = enderecoPessoaRepository.findByPessoa(pessoa);

        // Garante que os DTOs de endereço e documento são criados mesmo se as entidades estiverem ausentes
        EnderecoPessoaDTO enderecoDTO = endereco != null ? EnderecoPessoaDTO.fromEntity(endereco) : null;
        DocumentoPessoaDTO documentoDTO = documento != null ? DocumentoPessoaDTO.fromEntity(documento) : null;

        return Optional.of(new EditarDetentorResponseDTO(
                PessoaDTO.fromEntity(pessoa),
                PessoaLoteDTO.fromEntity(pl),
                enderecoDTO,
                documentoDTO
        ));
    }
    public Optional<EditarDetentorResponseDTO> getEditarDetentorPorLote(Long loteId) {
        return pessoaLoteRepository.findTopByLote_IdOrderByIdDesc(loteId).map(pl -> {
            Pessoa p = pl.getPessoa();
            DocumentoPessoa doc = documentoPessoaRepository.findByPessoa(p);
            EnderecoPessoa end = enderecoPessoaRepository.findByPessoa(p);
            return new EditarDetentorResponseDTO(
                    PessoaDTO.fromEntity(p),
                    PessoaLoteDTO.fromEntity(pl),
                    end != null ? EnderecoPessoaDTO.fromEntity(end) : null,
                    doc != null ? DocumentoPessoaDTO.fromEntity(doc) : null
            );
        });
    }



    @Transactional
    public PessoaDTO salvaDetentor(AtualizaDetentorRequestDTO dto) {
        // igual ao que te enviei antes (criação)
        Pessoa pessoa = pessoaRepository.save(dto.pessoa().toEntity());
        upsertVinculoEnderecoDocumento(pessoa, dto);
        return PessoaDTO.fromEntity(pessoa);
    }



    private void upsertVinculoEnderecoDocumento(Pessoa pessoa, AtualizaDetentorRequestDTO dto) {

        // Endereço
        if (dto.endereco() != null) {
            EnderecoPessoa endereco = dto.endereco().toEntity();
            endereco.setPessoa(pessoa);
            if (dto.endereco().municipioId() != null) {
                endereco.setMunicipio(municipioRepository.getReferenceById(dto.endereco().municipioId()));
            }
            enderecoPessoaRepository.findFirstByPessoaId(pessoa.getId()).ifPresent(ex -> endereco.setId(ex.getId()));
            enderecoPessoaRepository.save(endereco);
        }

        // Documento
        if (dto.documento() != null) {
            DocumentoPessoaDTO d = dto.documento();
            Municipio naturalidade = (d.naturalidadeId() != null)
                    ? municipioRepository.getReferenceById(d.naturalidadeId()) : null;

            DocumentoPessoa doc = d.toEntity(pessoa, naturalidade);
            documentoPessoaRepository.findFirstByPessoaId(pessoa.getId()).ifPresent(ex -> doc.setId(ex.getId()));
            documentoPessoaRepository.save(doc);
        }
    }




    @Transactional
    public EditarDetentorResponseDTO atualizaDetentor(Long pessoaLoteId, AtualizaDetentorRequestDTO dto)
            throws PessoaValidationException {

        PessoaLote pessoaLote = pessoaLoteRepository.findById(pessoaLoteId)
                .orElseThrow(() -> new PessoaValidationException("PessoaLote não encontrada!"));

        Pessoa pessoa = pessoaLote.getPessoa();

        // Atualiza dados básicos
        pessoa.setNome(dto.pessoa().nome());
        pessoa.setFax(dto.pessoa().fax());
        pessoa.setRamal(dto.pessoa().ramal());
        pessoa.setEmail(dto.pessoa().email());
        pessoa.setNomePai(dto.pessoa().nomePai());
        pessoa.setNomeMae(dto.pessoa().nomeMae());
        pessoa.setDataNascimento(dto.pessoa().dataNascimento());
        pessoa.setSexoPessoa(dto.pessoa().sexoPessoa());
        pessoa.setTelefone(dto.pessoa().telefone());
        pessoa.setRegimeDeBens(dto.pessoa().regimeDeBens());
        pessoa.setDataCasamento(dto.pessoa().dataCasamento());
        pessoa.setCodigoPessoaIncra(dto.pessoa().codigoPessoaIncra());
        pessoa.setCoordenadaEste(dto.pessoa().coordenadaEste());
        pessoa.setCoordenadaNorte(dto.pessoa().coordenadaNorte());
        pessoa.setAtividadePrincipal(dto.pessoa().atividadePrincipal());
        pessoa.setIsRecebePronaf(dto.pessoa().isRecebePronaf());
        pessoa.setIsRecebeAjudoProgramaGoverno(dto.pessoa().isRecebeAjudoProgramaGoverno());
        pessoa.setQtdPronaf(dto.pessoa().qtdPronaf());
        pessoa.setRacaCor(dto.pessoa().racaCor());
        pessoa.setValorTotalPronafs(dto.pessoa().valorTotalPronafs());
        pessoa.setIsEspolio(Boolean.TRUE.equals(dto.pessoa().isEspolio()));

        // Endereço
        EnderecoPessoa endereco = enderecoPessoaRepository.findByPessoa(pessoa);
        if (endereco == null) endereco = new EnderecoPessoa();
        endereco.setPessoa(pessoa);
        endereco.setLogradouro(dto.endereco().logradouro());
        endereco.setComplemento(dto.endereco().complemento());
        endereco.setNumero(dto.endereco().numero());
        endereco.setBairro(dto.endereco().bairro());
        endereco.setCep(dto.endereco().cep());
        endereco.setCodigoPaisResidencia(dto.endereco().codigoPaisResidencia());
        if (dto.endereco().municipioId() != null) {
            municipioRepository.findById(dto.endereco().municipioId())
                    .ifPresent(endereco::setMunicipio);
        }

        // Documento
        DocumentoPessoa documento = documentoPessoaRepository.findByPessoa(pessoa);
        if (documento == null) documento = new DocumentoPessoa();
        documento.setPessoa(pessoa);
        documento.setTipoDocumentoIdentificacao(dto.documento().tipoDocumentoIdentificacao());
        documento.setNumeroDocumentoIdentificacao(dto.documento().numeroDocumentoIdentificacao());
        documento.setOrgaoEmissor(dto.documento().orgaoEmissor());
        documento.setUfOrgaoEmissor(dto.documento().ufOrgaoEmissor());
        documento.setTipoNacionalidade(dto.documento().tipoNacionalidade());
        documento.setCpf(dto.documento().cpf());
        documento.setCnpj(dto.documento().cnpj());
        documento.setEstadoCivil(dto.documento().estadoCivil());
        documento.setTipoPessoa(dto.documento().tipoPessoa());
        documento.setNaturezaJuridica(dto.documento().naturezaJuridica());
        documento.setCapitalNacional(dto.documento().capitalNacional());
        documento.setCapitalEstrangeiro(dto.documento().capitalEstrangeiro());
        documento.setRegistroJuntaComercial(dto.documento().registroJuntaComercial());
        documento.setNomeFantasia(dto.documento().nomeFantasia());
        documento.setCodigoPaisSede(dto.documento().codigoPaisSede());
        documento.setUfPaisSede(dto.documento().ufPaisSede());
        documento.setTipoDocumentoRepresentanteLegal(dto.documento().tipoDocumentoRepresentanteLegal());
        documento.setNumeroDocumentoRepresentanteLegal(dto.documento().numeroDocumentoRepresentanteLegal());
        documento.setTipoDePoder(dto.documento().tipoDePoder());
        documento.setTipoDeGoverno(dto.documento().tipoDeGoverno());
        documento.setPercentCapitalNacional(dto.documento().percentCapitalNacional());
        documento.setPercentCapitalEstrangeiro(dto.documento().percentCapitalEstrangeiro());
        documento.setPcePais(dto.documento().pcePais());
        documento.setPcePercentCapital(dto.documento().pcePercentCapital());
        documento.setObsevacoesQuadro7(dto.documento().obsevacoesQuadro7());

        // Atualiza PessoaLote
        pessoaLote.setCodigoImovelRural(dto.pessoaLote().codigoImovelRural());
        pessoaLote.setCondicaoPessoaImovelRural(dto.pessoaLote().condicaoPessoaImovelRural());
        pessoaLote.setTipoDoAto(dto.pessoaLote().tipoDoAto());
        pessoaLote.setNumeroAto(dto.pessoaLote().numeroAto());
        pessoaLote.setDataAto(dto.pessoaLote().dataAto());
        pessoaLote.setPercentDetencao(dto.pessoaLote().percentDetencao());
        pessoaLote.setQuantidadeAreaCedida(dto.pessoaLote().quantidadeAreaCedida());
        pessoaLote.setAtividadePrincipalExploracao(dto.pessoaLote().atividadePrincipalExploracao());
        pessoaLote.setContrato(dto.pessoaLote().contrato());
        pessoaLote.setDataTerminoContrato(dto.pessoaLote().dataTerminoContrato());
        pessoaLote.setIsResideNoImovel(dto.pessoaLote().isResideNoImovel());
        pessoaLote.setIsDeclarante(dto.pessoaLote().isDeclarante());
        //dto.pessoaLote().isContratoPrazoIndeterminado()
        pessoaLote.setIsContratoPrazoIndeterminado(
                dto.pessoaLote().isContratoPrazoIndeterminado() != null ? dto.pessoaLote().isContratoPrazoIndeterminado() : false
        );


        // Validação: percentual de detenção não pode passar de 100%
        BigDecimal percentDetencaoTotal = pessoaLote.getLote().getPessoasLote().stream()
                .map(pl -> pl.equals(pessoaLote) ? dto.pessoaLote().percentDetencao() : pl.getPercentDetencao())
                .map(PessoaService::nz)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (percentDetencaoTotal.compareTo(new BigDecimal("100.00")) > 0) {
            throw new PessoaValidationException("Porcentagem de detenção total do imóvel excede 100%. Verifique os detentores.");
        }

        // Pronafs
        pessoa.getPronafs().clear();
        if (dto.pessoa().pronafsIds() != null) {
            for (Long pronafId : dto.pessoa().pronafsIds()) {
                pronafRepository.findById(pronafId).ifPresent(pessoa.getPronafs()::add);
            }
        }

        // Salva tudo
        pessoaRepository.save(pessoa);
        EnderecoPessoa enderecoPessoa = enderecoPessoaRepository.findByPessoa(pessoa);
        DocumentoPessoa documentoPessoa = documentoPessoaRepository.findByPessoa(pessoa);
        pessoaLoteRepository.save(pessoaLote);

        // Recarrega o objeto salvo e retorna o DTO atualizado
        Pessoa pessoaAtualizada = pessoaRepository.findById(pessoa.getId()).orElseThrow();
        return new EditarDetentorResponseDTO(
                PessoaDTO.fromEntity(pessoaAtualizada),
                PessoaLoteDTO.fromEntity(pessoaLote),
                enderecoPessoa != null ? EnderecoPessoaDTO.fromEntity(enderecoPessoa) : null,
                documentoPessoa != null ? DocumentoPessoaDTO.fromEntity(documentoPessoa) : null
        );
    }
}

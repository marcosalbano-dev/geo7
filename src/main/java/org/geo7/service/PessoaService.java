package org.geo7.service;

import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PessoaService {

    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private PessoaLoteRepository pessoaLoteRepository;
    @Autowired private EnderecoPessoaRepository enderecoPessoaRepository;
    @Autowired private DocumentoPessoaRepository documentoPessoaRepository;
    //@Autowired private ProgramaGovernoRepository programaGovernoRepository;
    @Autowired private PronafRepository pronafRepository;
    @Autowired private MunicipioRepository municipioRepository;
    @Autowired private LoteRepository loteRepository;

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

    @Transactional
    public void salvaDetentor(AtualizaDetentorRequestDTO dto) throws PessoaValidationException {
        // Cria nova Pessoa a partir do DTO
        Pessoa pessoa = dto.pessoa().toEntity();
        // Relacionamentos como programas do governo e pronafs devem ser associados após salvar pessoa

        // Salva Pessoa primeiro para obter o ID
        pessoa = pessoaRepository.save(pessoa);

        // Programas do Governo
//        pessoa.getProgramasDoGoverno().clear();
//        if (dto.pessoa().programasDoGovernoIds() != null) {
//            for (Long progId : dto.pessoa().programasDoGovernoIds()) {
//                programaGovernoRepository.findById(progId).ifPresent(pessoa.getProgramasDoGoverno()::add);
//            }
//        }

//        if (dto.pessoa().programasDoGovernoIds() != null) {
//            Set<ProgramaGoverno> programas = new HashSet<>(programaGovernoRepository.findAllById(dto.pessoa().programasDoGovernoIds()));
//            pessoa.setProgramasDoGoverno(programas);
//        } else {
//            pessoa.setProgramasDoGoverno(Collections.emptySet());
//        }

        // Pronafs
        pessoa.getPronafs().clear();
        if (dto.pessoa().pronafsIds() != null) {
            for (Long pronafId : dto.pessoa().pronafsIds()) {
                pronafRepository.findById(pronafId).ifPresent(pessoa.getPronafs()::add);
            }
        }

        pessoa = pessoaRepository.save(pessoa); // Atualiza as coleções

        // Cria e salva EnderecoPessoa
        EnderecoPessoa endereco = dto.endereco().toEntity();
        System.out.println("MUNICIPIO_ID: " + dto.endereco().municipioId());

        endereco.setPessoa(pessoa);
        if (dto.endereco().municipioId() != null) {
            municipioRepository.findById(dto.endereco().municipioId())
                    .ifPresent(endereco::setMunicipio);
        }

        endereco = enderecoPessoaRepository.save(endereco);

        // Cria e salva DocumentoPessoa
        DocumentoPessoa documento = dto.documento().toEntity(pessoa, endereco.getMunicipio());
        documento.setPessoa(pessoa);
        documento = documentoPessoaRepository.save(documento);

        // Cria PessoaLote (busca o lote pelo id recebido)
        if (dto.pessoaLote().loteId() == null)
            throw new PessoaValidationException("Lote obrigatório!");

        Lote lote = loteRepository.findById(dto.pessoaLote().loteId())
                .orElseThrow(() -> new PessoaValidationException("Lote não encontrado!"));

        PessoaLote pessoaLote = dto.pessoaLote().toEntity(pessoa, lote);
        pessoaLote.setPessoa(pessoa);
        pessoaLote.setLote(lote);

        // Validação: percentual de detenção não pode passar de 100%
        BigDecimal totalPercent = lote.getPessoasLote().stream()
                .map(pl -> pl.getPercentDetencao() == null ? BigDecimal.ZERO : pl.getPercentDetencao())
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .add(pessoaLote.getPercentDetencao() == null ? BigDecimal.ZERO : pessoaLote.getPercentDetencao());


        if (totalPercent.compareTo(new BigDecimal("100.00")) > 0) {
            throw new PessoaValidationException("Porcentagem de detenção total do imóvel excede 100%. Verifique os detentores.");
        }

        pessoaLote = pessoaLoteRepository.save(pessoaLote);

        // Tudo pronto!
    }



    @Transactional
    public void atualizaDetentor(Long pessoaLoteId, AtualizaDetentorRequestDTO dto) throws PessoaValidationException {
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

        // Programas do Governo
//        pessoa.getProgramasDoGoverno().clear();
//        if (dto.pessoa().programasDoGovernoIds() != null) {
//            for (Long progId : dto.pessoa().programasDoGovernoIds()) {
//                programaGovernoRepository.findById(progId).ifPresent(pessoa.getProgramasDoGoverno()::add);
//            }
//        }

        pessoa.setValorTotalPronafs(dto.pessoa().valorTotalPronafs());

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
        pessoaLote.setIsContratoPrazoIndeterminado(dto.pessoaLote().isContratoPrazoIndeterminado());

        // Validação: percentual de detenção não pode passar de 100%
        BigDecimal percentDetencaoTotal = pessoaLote.getLote().getPessoasLote().stream()
                .map(pl -> pl.equals(pessoaLote) ? dto.pessoaLote().percentDetencao() : pl.getPercentDetencao())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (percentDetencaoTotal.compareTo(new BigDecimal("100.00")) > 0) {
            throw new PessoaValidationException("Porcentagem de detenção total do imóvel excede 100%. Verifique os detentores.");
        }

        // Salva tudo
        pessoaRepository.save(pessoa);
        enderecoPessoaRepository.save(endereco);
        documentoPessoaRepository.save(documento);
        pessoaLoteRepository.save(pessoaLote);

        // Pronafs
        pessoa.getPronafs().clear();
        if (dto.pessoa().pronafsIds() != null) {
            for (Long pronafId : dto.pessoa().pronafsIds()) {
                pronafRepository.findById(pronafId).ifPresent(pessoa.getPronafs()::add);
            }
        }
    }

}

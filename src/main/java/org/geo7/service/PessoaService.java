package org.geo7.service;

import org.geo7.model.entity.*;
import org.geo7.model.repository.*;
import org.geo7.rest.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PessoaService {

    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private PessoaLoteRepository pessoaLoteRepository;
    @Autowired private EnderecoPessoaRepository enderecoPessoaRepository;
    @Autowired private DocumentoPessoaRepository documentoPessoaRepository;
    @Autowired private ProgramaGovernoRepository programaGovernoRepository;
    @Autowired private PronafRepository pronafRepository;
    @Autowired private MunicipioRepository municipioRepository;

    public Optional<EditarDetentorResponseDTO> getEditarDetentorData(Long pessoaLoteId) {
        Optional<PessoaLote> pessoaLoteOpt = pessoaLoteRepository.findById(pessoaLoteId);
        if (pessoaLoteOpt.isEmpty()) return Optional.empty();

        PessoaLote pl = pessoaLoteOpt.get();
        Pessoa pessoa = pl.getPessoa();
        DocumentoPessoa documento = documentoPessoaRepository.findByPessoa(pessoa);
        EnderecoPessoa endereco = enderecoPessoaRepository.findByPessoa(pessoa);

        return Optional.of(new EditarDetentorResponseDTO(
                PessoaDTO.fromEntity(pessoa),
                PessoaLoteDTO.fromEntity(pl),
                EnderecoPessoaDTO.fromEntity(endereco),
                DocumentoPessoaDTO.fromEntity(documento)
        ));
    }

    @Transactional
    public void atualizaDetentor(Long pessoaLoteId, AtualizaDetentorRequestDTO dto) throws PessoaValidationException {
        PessoaLote pessoaLote = pessoaLoteRepository.findById(pessoaLoteId)
                .orElseThrow(() -> new PessoaValidationException("PessoaLote não encontrada!"));
        Pessoa pessoa = pessoaLote.getPessoa();

        // Atualiza dados básicos
        pessoa.setNome(dto.getPessoa().nome());
        pessoa.setFax(dto.getPessoa().fax());
        pessoa.setRamal(dto.getPessoa().ramal());
        pessoa.setEmail(dto.getPessoa().email());
        pessoa.setNomePai(dto.getPessoa().nomePai());
        pessoa.setNomeMae(dto.getPessoa().nomeMae());
        pessoa.setDataNascimento(dto.getPessoa().dataNascimento());
        pessoa.setSexoPessoa(dto.getPessoa().sexoPessoa());
        pessoa.setTelefone(dto.getPessoa().telefone());
        pessoa.setRegimeDeBens(dto.getPessoa().regimeDeBens());
        pessoa.setDataCasamento(dto.getPessoa().dataCasamento());
        pessoa.setCodigoPessoaIncra(dto.getPessoa().codigoPessoaIncra());
        pessoa.setCoordenadaEste(dto.getPessoa().coordenadaEste());
        pessoa.setCoordenadaNorte(dto.getPessoa().coordenadaNorte());
        pessoa.setAtividadePrincipal(dto.getPessoa().atividadePrincipal());
        pessoa.setIsRecebePronaf(dto.getPessoa().isRecebePronaf());
        pessoa.setIsRecebeAjudoProgramaGoverno(dto.getPessoa().isRecebeAjudoProgramaGoverno());
        pessoa.setQtdPronaf(dto.getPessoa().qtdPronaf());
        pessoa.setRacaCor(dto.getPessoa().racaCor());

        // Programas do Governo
        pessoa.getProgramasDoGoverno().clear();
        if (dto.getPessoa().programasDoGovernoIds() != null) {
            for (Long progId : dto.getPessoa().programasDoGovernoIds()) {
                programaGovernoRepository.findById(progId).ifPresent(pessoa.getProgramasDoGoverno()::add);
            }
        }

        pessoa.setValorTotalPronafs(dto.getPessoa().valorTotalPronafs());

        // Endereço
        EnderecoPessoa endereco = enderecoPessoaRepository.findByPessoa(pessoa);
        if (endereco == null) endereco = new EnderecoPessoa();
        endereco.setPessoa(pessoa);
        endereco.setLogradouro(dto.getEndereco().logradouro());
        endereco.setComplemento(dto.getEndereco().complemento());
        endereco.setNumero(dto.getEndereco().numero());
        endereco.setBairro(dto.getEndereco().bairro());
        endereco.setCep(dto.getEndereco().cep());
        endereco.setCodigoPaisResidencia(dto.getEndereco().codigoPaisResidencia());
        if (dto.getEndereco().municipioId() != null) {
            municipioRepository.findById(dto.getEndereco().municipioId())
                    .ifPresent(endereco::setMunicipio);
        }

        // Documento
        DocumentoPessoa documento = documentoPessoaRepository.findByPessoa(pessoa);
        if (documento == null) documento = new DocumentoPessoa();
        documento.setPessoa(pessoa);
        documento.setTipoDocumentoIdentificacao(dto.getDocumento().tipoDocumentoIdentificacao());
        documento.setNumeroDocumentoIdentificacao(dto.getDocumento().numeroDocumentoIdentificacao());
        documento.setOrgaoEmissor(dto.getDocumento().orgaoEmissor());
        documento.setUfOrgaoEmissor(dto.getDocumento().ufOrgaoEmissor());
        documento.setTipoNacionalidade(dto.getDocumento().tipoNacionalidade());
        documento.setCpf(dto.getDocumento().cpf());
        documento.setCnpj(dto.getDocumento().cnpj());
        documento.setEstadoCivil(dto.getDocumento().estadoCivil());
        documento.setTipoPessoa(dto.getDocumento().tipoPessoa());
        documento.setNaturezaJuridica(dto.getDocumento().naturezaJuridica());
        documento.setCapitalNacional(dto.getDocumento().capitalNacional());
        documento.setCapitalEstrangeiro(dto.getDocumento().capitalEstrangeiro());
        documento.setRegistroJuntaComercial(dto.getDocumento().registroJuntaComercial());
        documento.setNomeFantasia(dto.getDocumento().nomeFantasia());
        documento.setCodigoPaisSede(dto.getDocumento().codigoPaisSede());
        documento.setUfPaisSede(dto.getDocumento().ufPaisSede());
        documento.setTipoDocumentoRepresentanteLegal(dto.getDocumento().tipoDocumentoRepresentanteLegal());
        documento.setNumeroDocumentoRepresentanteLegal(dto.getDocumento().numeroDocumentoRepresentanteLegal());
        documento.setTipoDePoder(dto.getDocumento().tipoDePoder());
        documento.setTipoDeGoverno(dto.getDocumento().tipoDeGoverno());
        documento.setPercentCapitalNacional(dto.getDocumento().percentCapitalNacional());
        documento.setPercentCapitalEstrangeiro(dto.getDocumento().percentCapitalEstrangeiro());
        documento.setPcePais(dto.getDocumento().pcePais());
        documento.setPcePercentCapital(dto.getDocumento().pcePercentCapital());
        documento.setObsevacoesQuadro7(dto.getDocumento().obsevacoesQuadro7());

        // Atualiza PessoaLote
        pessoaLote.setCodigoImovelRural(dto.getPessoaLote().codigoImovelRural());
        pessoaLote.setCondicaoPessoaImovelRural(dto.getPessoaLote().condicaoPessoaImovelRural());
        pessoaLote.setTipoDoAto(dto.getPessoaLote().tipoDoAto());
        pessoaLote.setNumeroAto(dto.getPessoaLote().numeroAto());
        pessoaLote.setDataAto(dto.getPessoaLote().dataAto());
        pessoaLote.setPercentDetencao(dto.getPessoaLote().percentDetencao());
        pessoaLote.setQuantidadeAreaCedida(dto.getPessoaLote().quantidadeAreaCedida());
        pessoaLote.setAtividadePrincipalExploracao(dto.getPessoaLote().atividadePrincipalExploracao());
        pessoaLote.setContrato(dto.getPessoaLote().contrato());
        pessoaLote.setDataTerminoContrato(dto.getPessoaLote().dataTerminoContrato());
        pessoaLote.setIsResideNoImovel(dto.getPessoaLote().isResideNoImovel());
        pessoaLote.setIsDeclarante(dto.getPessoaLote().isDeclarante());
        pessoaLote.setIsContratoPrazoIndeterminado(dto.getPessoaLote().isContratoPrazoIndeterminado());

        // Validação: percentual de detenção não pode passar de 100%
        BigDecimal percentDetencaoTotal = pessoaLote.getLote().getPessoasLote().stream()
                .map(pl -> pl.equals(pessoaLote) ? dto.getPessoaLote().percentDetencao() : pl.getPercentDetencao())
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
        if (dto.getPessoa().pronafIds() != null) {
            for (Long pronafId : dto.getPessoa().pronafIds()) {
                pronafRepository.findById(pronafId).ifPresent(pessoa.getPronafs()::add);
            }
        }
    }
}

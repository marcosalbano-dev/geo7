package org.geo7.dto;

import java.util.List;

public record Geo7LoteAgregado(
        LoteDTO lote,
        EnderecoLoteDTO enderecoLote,
        EstruturaDTO estrutura,
        List<FormaObtencaoDTO> formas,
        DadosSobreUsoDTO dadosSobreUso,
        List<Geo7PessoaAgregada> pessoas
) {}

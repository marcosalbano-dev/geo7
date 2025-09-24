package org.geo7.dto;

import java.util.List;

public record Geo7MunicipioExportDTO(
        MunicipioDTO municipio,
        List<Geo7LoteAgregado> lotes
) {}

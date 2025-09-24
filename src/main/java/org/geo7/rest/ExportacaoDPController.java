package org.geo7.rest;

import lombok.RequiredArgsConstructor;
import org.geo7.dto.Geo7MunicipioExportDTO;
import org.geo7.service.ExportacaoDpService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exportacao-dp")
@RequiredArgsConstructor
public class ExportacaoDPController {

    private final ExportacaoDpService service;

    // GET /exportacao-dp/municipio/{id} -> JSON agregado para o front gerar o XML
    @GetMapping("/municipio/{municipioId}")
    public ResponseEntity<Geo7MunicipioExportDTO> getMunicipioAgregado(@PathVariable Long municipioId) {
        return ResponseEntity.ok(service.carregarMunicipioAgregado(municipioId));
    }

    // OPCIONAL: GET /exportacao-dp/municipio/{id}/xml -> XML direto do back
    @GetMapping(value = "/municipio/{municipioId}/xml", produces = "application/xml")
    public ResponseEntity<String> getMunicipioXml(@PathVariable Long municipioId) {
        var dto = service.carregarMunicipioAgregado(municipioId);
        String xml = service.gerarXml(dto); // usa um writer interno (ver abaixo)
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=" +
                        (dto.municipio().nome().replaceAll("\\s+", "_").toUpperCase()) + ".xml")
                .body(xml);
    }
}

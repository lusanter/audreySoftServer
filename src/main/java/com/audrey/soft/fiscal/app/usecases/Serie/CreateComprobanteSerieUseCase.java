package com.audrey.soft.fiscal.app.usecases.Serie;

import com.audrey.soft.fiscal.app.dtos.ComprobanteSerieDTO;
import com.audrey.soft.fiscal.domain.models.ComprobanteSerie;
import com.audrey.soft.fiscal.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalConfigRepository;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalSistemaRepository;

import java.util.UUID;

public class CreateComprobanteSerieUseCase {

    private final ComprobanteSerieRepositoryPort serieRepository;
    private final SpringDataFiscalConfigRepository fiscalConfigRepository;
    private final SpringDataFiscalSistemaRepository fiscalSistemaRepository;

    public CreateComprobanteSerieUseCase(ComprobanteSerieRepositoryPort serieRepository,
                                         SpringDataFiscalConfigRepository fiscalConfigRepository,
                                         SpringDataFiscalSistemaRepository fiscalSistemaRepository) {
        this.serieRepository = serieRepository;
        this.fiscalConfigRepository = fiscalConfigRepository;
        this.fiscalSistemaRepository = fiscalSistemaRepository;
    }

    public ComprobanteSerieDTO execute(UUID sucursalId, ComprobanteSerieDTO request) {
        String serie = request.serie().toUpperCase();

        // Validate serie format against the fiscal system's serie_regex (if configured)
        fiscalConfigRepository.findBySucursalId(sucursalId).ifPresent(config -> {
            String sistemaId = config.getFiscalSistemaId();
            if (sistemaId != null && !"INTERNO".equals(sistemaId)) {
                fiscalSistemaRepository.findById(sistemaId).ifPresent(sistema -> {
                    String regex = sistema.getSerieRegex();
                    if (regex != null && !regex.isBlank() && !serie.matches(regex)) {
                        throw new IllegalArgumentException(
                                "El formato de la serie '" + serie + "' no es válido para el sistema " +
                                sistemaId + ". Formato esperado: " + sistema.getSerieFormato());
                    }
                });
            }
        });

        ComprobanteSerie nueva = new ComprobanteSerie(null, sucursalId,
                request.tipoComprobante(), serie,
                0, 99999999, true, null, null);
        ComprobanteSerie guardada = serieRepository.save(nueva);
        return toDto(guardada);
    }

    private ComprobanteSerieDTO toDto(ComprobanteSerie s) {
        return new ComprobanteSerieDTO(s.getId(), s.getSucursalId(), s.getTipoComprobante(),
                s.getSerie(), s.getCorrelativoActual(), s.getCorrelativoMax(),
                s.isActivo(), s.getCreatedAt());
    }
}

package com.audrey.soft.billing.app.usecases.Serie;

import com.audrey.soft.billing.app.dtos.ComprobanteSerieDTO;
import com.audrey.soft.billing.domain.models.ComprobanteSerie;
import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;

import java.util.UUID;

public class CreateComprobanteSerieUseCase {
    private final ComprobanteSerieRepositoryPort serieRepository;

    public CreateComprobanteSerieUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        this.serieRepository = serieRepository;
    }

    public ComprobanteSerieDTO execute(UUID sucursalId, ComprobanteSerieDTO request) {
        ComprobanteSerie nueva = new ComprobanteSerie(null, sucursalId,
                request.tipoComprobante(), request.serie().toUpperCase(),
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

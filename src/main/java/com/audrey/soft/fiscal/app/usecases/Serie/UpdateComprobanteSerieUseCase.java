package com.audrey.soft.fiscal.app.usecases.Serie;

import com.audrey.soft.fiscal.app.dtos.ComprobanteSerieDTO;
import com.audrey.soft.fiscal.domain.ports.ComprobanteSerieRepositoryPort;

import java.util.UUID;

public class UpdateComprobanteSerieUseCase {
    private final ComprobanteSerieRepositoryPort serieRepository;

    public UpdateComprobanteSerieUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        this.serieRepository = serieRepository;
    }

    public ComprobanteSerieDTO execute(UUID serieId, ComprobanteSerieDTO request) {
        var serie = serieRepository.findById(serieId)
                .orElseThrow(() -> new RuntimeException("Serie no encontrada: " + serieId));

        serie.setActivo(request.activo() != null ? request.activo() : serie.isActivo());
        serie.setCorrelativoMax(request.correlativoMax() != null ? request.correlativoMax() : serie.getCorrelativoMax());

        var guardada = serieRepository.save(serie);
        return new ComprobanteSerieDTO(guardada.getId(), guardada.getSucursalId(),
                guardada.getTipoComprobante(), guardada.getSerie(),
                guardada.getCorrelativoActual(), guardada.getCorrelativoMax(),
                guardada.isActivo(), guardada.getCreatedAt());
    }
}

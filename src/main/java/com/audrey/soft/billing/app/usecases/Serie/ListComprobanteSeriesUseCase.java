package com.audrey.soft.billing.app.usecases.Serie;

import com.audrey.soft.billing.app.dtos.ComprobanteSerieDTO;
import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListComprobanteSeriesUseCase {
    private final ComprobanteSerieRepositoryPort serieRepository;

    public ListComprobanteSeriesUseCase(ComprobanteSerieRepositoryPort serieRepository) {
        this.serieRepository = serieRepository;
    }

    public List<ComprobanteSerieDTO> execute(UUID sucursalId) {
        return serieRepository.findBySucursalId(sucursalId).stream()
                .map(s -> new ComprobanteSerieDTO(s.getId(), s.getSucursalId(), s.getTipoComprobante(),
                        s.getSerie(), s.getCorrelativoActual(), s.getCorrelativoMax(),
                        s.isActivo(), s.getCreatedAt()))
                .toList();
    }
}

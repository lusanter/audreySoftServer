package com.audrey.soft.fiscal.domain.ports;

import com.audrey.soft.fiscal.domain.models.ComprobanteSerie;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ComprobanteSerieRepositoryPort {
    ComprobanteSerie save(ComprobanteSerie serie);
    Optional<ComprobanteSerie> findById(UUID id);
    List<ComprobanteSerie> findBySucursalId(UUID sucursalId);
    Optional<ComprobanteSerie> findActivaByTipoAndSucursal(String tipoComprobante, UUID sucursalId);
    // Incremento atómico con SELECT FOR UPDATE — devuelve el nuevo correlativo
    int incrementarCorrelativo(UUID serieId);
}

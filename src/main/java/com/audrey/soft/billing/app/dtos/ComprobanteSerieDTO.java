package com.audrey.soft.billing.app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ComprobanteSerieDTO(
        UUID id,
        UUID sucursalId,
        String tipoComprobante,
        String serie,
        int correlativoActual,
        int correlativoMax,
        boolean activo,
        LocalDateTime createdAt
) {}

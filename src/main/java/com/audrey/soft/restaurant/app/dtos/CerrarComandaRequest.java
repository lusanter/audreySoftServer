package com.audrey.soft.restaurant.app.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CerrarComandaRequest(
        String tipoComprobante,          // BOLETA | FACTURA | NOTA_VENTA
        UUID clienteId,                  // opcional
        BigDecimal descuento,            // opcional, default 0
        List<CobroRequest> cobros        // al menos uno requerido
) {}

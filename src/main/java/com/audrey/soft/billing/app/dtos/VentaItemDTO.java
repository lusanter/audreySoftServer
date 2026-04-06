package com.audrey.soft.billing.app.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record VentaItemDTO(
        UUID id,
        UUID productoId,
        String nombreProducto,
        BigDecimal cantidad,
        BigDecimal precioUnitario
) {}

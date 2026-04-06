package com.audrey.soft.billing.app.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record VentaCobroDTO(
        UUID id,
        UUID metodoCobro,
        BigDecimal monto,
        String referencia
) {}

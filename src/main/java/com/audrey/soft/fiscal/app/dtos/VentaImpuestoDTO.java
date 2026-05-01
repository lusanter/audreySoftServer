package com.audrey.soft.fiscal.app.dtos;

import java.math.BigDecimal;

public record VentaImpuestoDTO(
        String codigo,
        String nombre,
        BigDecimal tasa,
        BigDecimal monto
) {}

package com.audrey.soft.restaurant.app.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record CobroRequest(
        UUID metodoCobro,
        BigDecimal monto,
        String referencia    // número de operación Yape/Plin/transferencia, opcional
) {}

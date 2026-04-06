package com.audrey.soft.restaurant.app.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record AgregarItemRequest(
        UUID productoId,
        BigDecimal cantidad,
        String notas
) {}

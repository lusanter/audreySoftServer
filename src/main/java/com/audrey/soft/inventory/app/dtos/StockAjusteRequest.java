package com.audrey.soft.inventory.app.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record StockAjusteRequest(
        UUID productoId,
        UUID motivoId,
        BigDecimal cantidad,
        String nota
) {}

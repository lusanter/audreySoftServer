package com.audrey.soft.inventory.app.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record StockEntradaRequest(
        UUID productoId,
        BigDecimal cantidad,
        BigDecimal precioCosto,
        String nota
) {}

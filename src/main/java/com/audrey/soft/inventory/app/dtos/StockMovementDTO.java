package com.audrey.soft.inventory.app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record StockMovementDTO(
        UUID id,
        UUID productoId,
        String nombreProducto,
        String tipo,
        BigDecimal cantidad,
        BigDecimal precioCosto,
        UUID referenciaId,
        String motivoNombre,
        String motivoTipo,
        String nota,
        LocalDateTime createdAt
) {}

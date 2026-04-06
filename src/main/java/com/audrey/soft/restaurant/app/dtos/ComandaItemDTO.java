package com.audrey.soft.restaurant.app.dtos;

import com.audrey.soft.restaurant.domain.models.EstadoItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ComandaItemDTO(
        UUID id,
        UUID comandaId,
        UUID productoId,
        String nombreProducto,
        BigDecimal cantidad,
        BigDecimal precioUnitario,
        String notas,
        String subCuenta,
        EstadoItem estado,
        LocalDateTime createdAt
) {}

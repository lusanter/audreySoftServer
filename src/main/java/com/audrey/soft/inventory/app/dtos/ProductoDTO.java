package com.audrey.soft.inventory.app.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ProductoDTO(
        UUID id,
        UUID sucursalId,
        UUID categoriaId,
        String nombre,
        String descripcion,
        BigDecimal precio,
        BigDecimal precioCosto,
        BigDecimal stockActual,
        BigDecimal stockMinimo,
        String unidad,
        boolean active,
        Boolean controlStock,
        LocalDateTime createdAt
) {}

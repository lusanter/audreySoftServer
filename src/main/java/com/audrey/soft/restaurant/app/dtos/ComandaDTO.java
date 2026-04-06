package com.audrey.soft.restaurant.app.dtos;

import com.audrey.soft.restaurant.domain.models.EstadoComanda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record ComandaDTO(
        UUID id,
        UUID sucursalId,
        UUID mesaId,
        UUID clienteId,
        EstadoComanda estado,
        BigDecimal total,
        String notas,
        List<ComandaItemDTO> items,
        LocalDateTime createdAt,
        LocalDateTime closedAt
) {}

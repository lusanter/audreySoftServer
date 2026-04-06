package com.audrey.soft.restaurant.app.dtos;

import com.audrey.soft.restaurant.domain.models.EstadoMesa;

import java.time.LocalDateTime;
import java.util.UUID;

public record MesaDTO(
        UUID id,
        UUID sucursalId,
        int numero,
        int capacidad,
        String zona,
        EstadoMesa estado,
        boolean active,
        LocalDateTime createdAt
) {}

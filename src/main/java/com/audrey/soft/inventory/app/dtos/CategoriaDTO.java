package com.audrey.soft.inventory.app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record CategoriaDTO(
        UUID id,
        UUID sucursalId,
        String nombre,
        boolean active,
        LocalDateTime createdAt
) {}

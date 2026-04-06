package com.audrey.soft.inventory.app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClienteDTO(
        UUID id,
        UUID sucursalId,
        String nombre,
        String documento,
        String email,
        String telefono,
        boolean active,
        LocalDateTime createdAt
) {}

package com.audrey.soft.tenant.app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record SucursalDTO(
        UUID id,
        UUID empresaId,
        String nombre,
        String direccion,
        boolean active,
        LocalDateTime createdAt
) {
    public SucursalDTO {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre de la sucursal no puede estar vacío");
    }
}

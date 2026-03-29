package com.audrey.soft.tenant.app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;
import com.audrey.soft.tenant.domain.models.VerticalType;

public record SucursalDTO(
        UUID id,
        UUID empresaId,
        String nombre,
        String direccion,
        VerticalType vertical,
        boolean active,
        LocalDateTime createdAt) {
    public SucursalDTO {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre de la sucursal no puede estar vacío");
        if (vertical == null)
            throw new IllegalArgumentException("El tipo vertical de negocio en la Sucursal es obligatorio");
    }
}

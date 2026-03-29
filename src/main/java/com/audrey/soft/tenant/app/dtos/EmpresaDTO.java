package com.audrey.soft.tenant.app.dtos;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmpresaDTO(
        UUID id,
        String nombre,
        String ruc,
        boolean active,
        LocalDateTime createdAt
) {
    public EmpresaDTO {
        if (nombre == null || nombre.isBlank())
            throw new IllegalArgumentException("El nombre de la empresa no puede estar vacío");
        if (ruc == null || ruc.isBlank())
            throw new IllegalArgumentException("El RUC no puede estar vacío");
    }
}

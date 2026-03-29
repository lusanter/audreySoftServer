package com.audrey.soft.auth.application.dtos;

import java.util.UUID;

public record SelectBranchRequestDTO(UUID sucursalId) {
    public SelectBranchRequestDTO {
        if (sucursalId == null)
            throw new IllegalArgumentException("El ID de sucursal no puede ser nulo");
    }
}

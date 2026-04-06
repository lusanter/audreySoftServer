package com.audrey.soft.billing.app.dtos;

import java.util.UUID;

public record MetodoCobroDTO(
        UUID id,
        UUID sucursalId,
        String nombre,
        String codigo,
        boolean activo
) {}

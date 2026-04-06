package com.audrey.soft.inventory.app.dtos;

import java.util.UUID;

public record AjusteMotivoDTO(
        UUID id,
        String nombre,
        String tipo,
        boolean active
) {}

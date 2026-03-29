package com.audrey.soft.auth.application.dtos;

import java.util.UUID;

/**
 * Sucursal disponible para un usuario — incluida en la respuesta del login
 * para usuarios con globalRole = SUCURSAL_ROLE.
 */
public record SucursalDisponibleDTO(
        UUID id,
        String nombre,
        String rol    // SucursalRole.name()
) {}

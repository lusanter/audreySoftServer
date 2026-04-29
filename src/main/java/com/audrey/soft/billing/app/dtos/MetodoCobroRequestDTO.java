package com.audrey.soft.billing.app.dtos;

public record MetodoCobroRequestDTO(
        String nombre,
        String codigo,
        String imagenUrl,
        boolean activo
) {}

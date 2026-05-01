package com.audrey.soft.fiscal.app.dtos;

public record FiscalSistemaDTO(
        String id,
        String nombre,
        String paisCodigo,
        String monedaDefault,
        String serieFormato,
        String serieRegex,
        int correlativoPadding,
        String separador,
        boolean activo
) {}

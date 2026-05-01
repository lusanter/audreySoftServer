package com.audrey.soft.fiscal.app.dtos;

import java.math.BigDecimal;

public record ImpuestoTipoDTO(
        String id,
        String fiscalSistemaId,
        String codigo,
        String nombre,
        BigDecimal tasaDefault,
        String tipoCalculo,
        Boolean activo
) {}

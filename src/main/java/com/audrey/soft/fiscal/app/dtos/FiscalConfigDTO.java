package com.audrey.soft.fiscal.app.dtos;

import java.util.UUID;

public record FiscalConfigDTO(
        UUID sucursalId,
        String fiscalSistemaId,
        String monedaCodigo,
        String rucEmpresa,
        String razonSocial,
        String direccionFiscal,
        String[] impuestosDefault,
        boolean preciosIncluyenImpuesto
) {}

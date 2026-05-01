package com.audrey.soft.billing.app.dtos;

import java.time.LocalDate;

public record VentaFiltroDTO(
        LocalDate desde,
        LocalDate hasta,
        String estado,
        String tipoComprobante,
        String serie,
        Boolean fiscalEnviado
) {}

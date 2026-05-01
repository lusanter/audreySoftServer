package com.audrey.soft.billing.app.dtos;

import com.audrey.soft.fiscal.app.dtos.VentaImpuestoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record VentaDTO(
        UUID id,
        UUID sucursalId,
        String tipoOrigen,
        UUID origenId,
        UUID clienteId,
        String tipoComprobante,
        String serie,
        Integer correlativo,
        String numeroComprobante,
        BigDecimal subtotal,
        BigDecimal descuento,
        BigDecimal totalImpuestos,
        BigDecimal total,
        String estado,
        boolean fiscalEnviado,
        String fiscalSistemaId,
        List<VentaImpuestoDTO> impuestos,
        List<VentaItemDTO> items,
        List<VentaCobroDTO> cobros,
        LocalDateTime createdAt
) {}

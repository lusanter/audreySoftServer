package com.audrey.soft.billing.app.dtos;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record CreateVentaDirectaRequest(
        UUID clienteId,                  // null = Clientes Varios
        String tipoComprobante,          // NOTA_VENTA | BOLETA | FACTURA
        BigDecimal descuento,            // 0 si no hay
        List<ItemRequest> items,
        List<CobroRequest> cobros
) {
    public record ItemRequest(
            UUID productoId,
            String nombreProducto,
            BigDecimal cantidad,
            BigDecimal precioUnitario
    ) {}

    public record CobroRequest(
            UUID metodoCobro,
            BigDecimal monto,
            String referencia
    ) {}
}

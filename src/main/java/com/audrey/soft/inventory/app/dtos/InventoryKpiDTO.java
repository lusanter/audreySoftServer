package com.audrey.soft.inventory.app.dtos;

import java.math.BigDecimal;
import java.util.UUID;

public record InventoryKpiDTO(
        UUID sucursalId,
        long totalProductos,
        long productosActivos,
        long productosStockCritico,
        BigDecimal valorInventario,
        BigDecimal margenEstimado,
        BigDecimal perdidaMermas,
        long entradasPeriodo,
        long salidasPeriodo
) {}

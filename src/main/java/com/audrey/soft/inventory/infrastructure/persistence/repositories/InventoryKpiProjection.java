package com.audrey.soft.inventory.infrastructure.persistence.repositories;

import java.math.BigDecimal;

public interface InventoryKpiProjection {
    Long getTotal_productos();
    Long getProductos_activos();
    Long getProductos_stock_critico();
    BigDecimal getValor_inventario();
    BigDecimal getMargen_estimado();
    BigDecimal getPerdida_mermas();
    Long getEntradas_periodo();
    Long getSalidas_periodo();
}

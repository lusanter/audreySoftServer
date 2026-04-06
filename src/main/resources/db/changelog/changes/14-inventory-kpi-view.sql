-- liquibase formatted sql

-- changeset santer:14-inventory-kpi-view runOnChange:true
CREATE OR REPLACE VIEW inventory.v_kpis_sucursal AS
SELECT
    p.sucursal_id,
    COUNT(*)                                                                             AS total_productos,
    COUNT(*) FILTER (WHERE p.active)                                                     AS productos_activos,
    COUNT(*) FILTER (WHERE p.active AND p.control_stock
                           AND p.stock_actual <= p.stock_minimo)                         AS productos_stock_critico,
    COALESCE(SUM(p.stock_actual * p.precio_costo)
             FILTER (WHERE p.active AND p.control_stock), 0)                             AS valor_inventario,
    COALESCE(SUM((p.precio - p.precio_costo) * p.stock_actual)
             FILTER (WHERE p.active AND p.control_stock), 0)                             AS margen_estimado,
    COUNT(sm.id) FILTER (WHERE sm.created_at >= NOW() - INTERVAL '7 days'
                           AND sm.tipo = 'ENTRADA')                                      AS entradas_semana,
    COUNT(sm.id) FILTER (WHERE sm.created_at >= NOW() - INTERVAL '7 days'
                           AND sm.tipo = 'SALIDA')                                       AS salidas_semana
FROM inventory.productos p
LEFT JOIN inventory.stock_movements sm ON sm.producto_id = p.id
GROUP BY p.sucursal_id;
-- rollback DROP VIEW IF EXISTS inventory.v_kpis_sucursal;

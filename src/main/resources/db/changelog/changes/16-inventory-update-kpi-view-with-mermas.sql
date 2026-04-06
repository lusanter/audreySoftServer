-- liquibase formatted sql

-- changeset santer:16-inventory-update-kpi-view-with-mermas
-- Actualizamos la vista de reporte para incluir la pérdida económica por mermas (sumatoria de stock descontado por precio costo)
DROP VIEW IF EXISTS inventory.v_kpis_sucursal;
CREATE OR REPLACE VIEW inventory.v_kpis_sucursal AS
SELECT 
    s.id AS sucursal_id,
    COUNT(p.id) AS total_productos,
    COUNT(p.id) FILTER (WHERE p.active = true) AS productos_activos,
    COUNT(p.id) FILTER (WHERE p.active = true AND p.control_stock = true AND p.stock_actual <= p.stock_minimo) AS productos_stock_critico,
    COALESCE(SUM(p.stock_actual * p.precio_costo) FILTER (WHERE p.active = true AND p.control_stock = true), 0) AS valor_inventario,
    COALESCE(SUM(p.stock_actual * (p.precio - p.precio_costo)) FILTER (WHERE p.active = true AND p.control_stock = true), 0) AS margen_estimado,
    
    -- Pérdida por mermas/ajustes negativos del último mes (monto absoluto)
    COALESCE(ABS(SUM(sm.cantidad * sm.precio_costo) FILTER (
        WHERE sm.tipo = 'AJUSTE' AND sm.cantidad < 0 
        AND sm.created_at >= (now() - interval '30 days')
    )), 0) AS perdida_mermas,

    COALESCE(COUNT(sm.id) FILTER (WHERE sm.tipo = 'ENTRADA' AND sm.created_at >= (now() - interval '7 days')), 0) AS entradas_semana,
    COALESCE(COUNT(sm.id) FILTER (WHERE sm.tipo = 'SALIDA'  AND sm.created_at >= (now() - interval '7 days')), 0) AS salidas_semana
FROM core.sucursales s
LEFT JOIN inventory.productos p ON s.id = p.sucursal_id
LEFT JOIN inventory.stock_movements sm ON p.id = sm.producto_id
GROUP BY s.id;

package com.audrey.soft.inventory.infrastructure.persistence.repositories;

import com.audrey.soft.inventory.infrastructure.persistence.entities.ProductoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringDataProductoRepository extends JpaRepository<ProductoEntity, UUID> {
    List<ProductoEntity> findBySucursalId(UUID sucursalId);

    @Modifying
    @Query("UPDATE ProductoEntity p SET p.stockActual = p.stockActual - :cantidad WHERE p.id = :id")
    void decrementarStock(@Param("id") UUID id, @Param("cantidad") BigDecimal cantidad);

    @Modifying
    @Query("UPDATE ProductoEntity p SET p.stockActual = p.stockActual + :cantidad, p.precioCosto = :precioCosto WHERE p.id = :id")
    void incrementarStockYActualizarCosto(@Param("id") UUID id, @Param("cantidad") BigDecimal cantidad, @Param("precioCosto") BigDecimal precioCosto);

    @Query(value = """
            SELECT 
              (SELECT COUNT(id) FROM inventory.productos WHERE sucursal_id = cast(:sucursalId as uuid)) as total_productos,
              (SELECT COUNT(id) FROM inventory.productos WHERE sucursal_id = cast(:sucursalId as uuid) AND active = true) as productos_activos,
              (SELECT COUNT(id) FROM inventory.productos WHERE sucursal_id = cast(:sucursalId as uuid) AND active = true AND control_stock = true AND stock_actual <= stock_minimo) as productos_stock_critico,
              (SELECT COALESCE(SUM(stock_actual * precio_costo), 0) FROM inventory.productos WHERE sucursal_id = cast(:sucursalId as uuid) AND active = true AND control_stock = true) as valor_inventario,
              (SELECT COALESCE(SUM(stock_actual * (precio - precio_costo)), 0) FROM inventory.productos WHERE sucursal_id = cast(:sucursalId as uuid) AND active = true AND control_stock = true) as margen_estimado,
              (SELECT COALESCE(ABS(SUM(sm.cantidad * COALESCE(sm.precio_costo, p.precio_costo))), 0) 
               FROM inventory.stock_movements sm 
               JOIN inventory.productos p ON p.id = sm.producto_id 
               WHERE p.sucursal_id = cast(:sucursalId as uuid) 
                 AND sm.tipo = 'AJUSTE' AND sm.cantidad < 0 
                 AND sm.created_at::date BETWEEN :start AND :end) as perdida_mermas,
              (SELECT COUNT(sm.id) 
               FROM inventory.stock_movements sm 
               JOIN inventory.productos p ON p.id = sm.producto_id 
               LEFT JOIN inventory.ajuste_motivos am ON sm.ajuste_motivo_id = am.id
               WHERE p.sucursal_id = cast(:sucursalId as uuid) 
                 AND (sm.tipo = 'ENTRADA' OR (sm.tipo = 'AJUSTE' AND COALESCE(am.tipo, 'INCREMENTO') = 'INCREMENTO'))
                 AND sm.created_at::date BETWEEN :start AND :end) as entradas_periodo,
              (SELECT COUNT(sm.id) 
               FROM inventory.stock_movements sm 
               JOIN inventory.productos p ON p.id = sm.producto_id 
               LEFT JOIN inventory.ajuste_motivos am ON sm.ajuste_motivo_id = am.id
               WHERE p.sucursal_id = cast(:sucursalId as uuid) 
                 AND (sm.tipo = 'SALIDA' OR (sm.tipo = 'AJUSTE' AND am.tipo = 'DECREMENTO'))
                 AND sm.created_at::date BETWEEN :start AND :end) as salidas_periodo
            """, nativeQuery = true)
    InventoryKpiProjection findKpiBySucursalId(
            @Param("sucursalId") UUID sucursalId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}


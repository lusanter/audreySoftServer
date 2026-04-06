package com.audrey.soft.inventory.app.usecases.StockMovement;

import com.audrey.soft.inventory.app.dtos.InventoryKpiDTO;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.SpringDataProductoRepository;
import com.audrey.soft.inventory.infrastructure.persistence.repositories.InventoryKpiProjection;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Consulta la VIEW inventory.v_kpis_sucursal para obtener los KPIs
 * de inventario de una sucursal en una sola query SQL.
 */
public class GetInventoryKpiUseCase {

    private final SpringDataProductoRepository productoRepository;

    public GetInventoryKpiUseCase(SpringDataProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    public InventoryKpiDTO execute(UUID sucursalId, LocalDate start, LocalDate end) {
        // Por defecto: hoy
        if (start == null) start = LocalDate.now();
        if (end == null) end = LocalDate.now();

        InventoryKpiProjection row = productoRepository.findKpiBySucursalId(sucursalId, start, end);

        if (row == null) {
            return new InventoryKpiDTO(sucursalId, 0, 0, 0,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 0, 0);
        }

        return new InventoryKpiDTO(
                sucursalId,
                row.getTotal_productos() != null ? row.getTotal_productos() : 0L,
                row.getProductos_activos() != null ? row.getProductos_activos() : 0L,
                row.getProductos_stock_critico() != null ? row.getProductos_stock_critico() : 0L,
                row.getValor_inventario() != null ? row.getValor_inventario() : BigDecimal.ZERO,
                row.getMargen_estimado() != null ? row.getMargen_estimado() : BigDecimal.ZERO,
                row.getPerdida_mermas() != null ? row.getPerdida_mermas() : BigDecimal.ZERO,
                row.getEntradas_periodo() != null ? row.getEntradas_periodo() : 0L,
                row.getSalidas_periodo() != null ? row.getSalidas_periodo() : 0L
        );
    }
}

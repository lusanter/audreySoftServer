package com.audrey.soft.billing.app.usecases.Venta;

import com.audrey.soft.billing.app.dtos.CreateVentaDirectaRequest;
import com.audrey.soft.billing.app.dtos.VentaCobroDTO;
import com.audrey.soft.billing.app.dtos.VentaDTO;
import com.audrey.soft.billing.app.dtos.VentaItemDTO;
import com.audrey.soft.billing.domain.models.*;
import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CreateVentaDirectaUseCase {

    private final VentaRepositoryPort ventaRepository;
    private final ComprobanteSerieRepositoryPort serieRepository;
    private final ProductoRepositoryPort productoRepository;
    private final StockMovementRepositoryPort stockMovementRepository;

    public CreateVentaDirectaUseCase(VentaRepositoryPort ventaRepository,
                                     ComprobanteSerieRepositoryPort serieRepository,
                                     ProductoRepositoryPort productoRepository,
                                     StockMovementRepositoryPort stockMovementRepository) {
        this.ventaRepository = ventaRepository;
        this.serieRepository = serieRepository;
        this.productoRepository = productoRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    @Transactional
    public VentaDTO execute(UUID sucursalId, CreateVentaDirectaRequest req) {
        if (req.items() == null || req.items().isEmpty())
            throw new IllegalArgumentException("La venta debe tener al menos un item");
        if (req.cobros() == null || req.cobros().isEmpty())
            throw new IllegalArgumentException("La venta debe tener al menos un cobro");

        BigDecimal descuento = req.descuento() != null ? req.descuento() : BigDecimal.ZERO;

        // Calcular montos
        BigDecimal subtotal = req.items().stream()
                .map(i -> i.precioUnitario().multiply(i.cantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal base = subtotal.subtract(descuento);
        BigDecimal igv = base.multiply(new BigDecimal("0.18"))
                .divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
        BigDecimal total = base;

        // Obtener serie activa y reservar correlativo
        String tipoComp = req.tipoComprobante() != null ? req.tipoComprobante() : "NOTA_VENTA";
        var serie = serieRepository.findActivaByTipoAndSucursal(tipoComp, sucursalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay serie activa para " + tipoComp + " en esta sucursal. Configúrala en Inventario > Configuración > Comprobantes."));

        int correlativo = serieRepository.incrementarCorrelativo(serie.getId());
        String numeroComprobante = serie.getSerie() + "-" + String.format("%08d", correlativo);

        // Construir items
        List<VentaItem> items = req.items().stream().map(i ->
                new VentaItem(null, null, i.productoId(), i.nombreProducto(),
                        i.cantidad(), i.precioUnitario())
        ).toList();

        // Construir cobros
        List<VentaCobro> cobros = req.cobros().stream().map(c ->
                new VentaCobro(null, null, c.metodoCobro(), c.monto(), c.referencia(), LocalDateTime.now())
        ).toList();

        // Origen DIRECTO — no tiene origenId externo, usamos un UUID fijo de marcador
        // Para ventas directas el origen es opcional; pasamos null para no crear fila en venta_origen
        Venta venta = new Venta(
                null, sucursalId,
                serie.getId(),
                null,   // origen null = venta directa sin referencia externa
                req.clienteId(),
                tipoComp,
                serie.getSerie(), correlativo, numeroComprobante,
                subtotal, descuento, igv, total,
                items, cobros,
                LocalDateTime.now()
        );

        Venta saved = ventaRepository.save(venta);

        // Descontar stock por cada item que controle stock
        LocalDateTime ahora = LocalDateTime.now();
        req.items().forEach(item -> {
            var producto = productoRepository.findById(item.productoId()).orElse(null);
            if (producto != null && producto.isControlStock()) {
                stockMovementRepository.save(
                        item.productoId(),
                        "SALIDA",
                        item.cantidad().negate(),
                        producto.getPrecioCosto(),
                        saved.getId(),
                        null,
                        "Venta directa",
                        ahora
                );
            }
        });

        // Mapear respuesta
        var itemsDTO = saved.getItems().stream().map(i ->
                new VentaItemDTO(i.getId(), i.getProductoId(), i.getNombreProducto(),
                        i.getCantidad(), i.getPrecioUnitario())).toList();
        var cobrosDTO = saved.getCobros().stream().map(c ->
                new VentaCobroDTO(c.getId(), c.getMetodoCobro(), c.getMonto(), c.getReferencia())).toList();

        return new VentaDTO(saved.getId(), saved.getSucursalId(),
                null, null,
                saved.getClienteId(),
                saved.getTipoComprobante(), saved.getSerie(), saved.getCorrelativo(), saved.getNumeroComprobante(),
                saved.getSubtotal(), saved.getDescuento(), saved.getIgv(), saved.getTotal(),
                "COBRADA", false, itemsDTO, cobrosDTO, saved.getCreatedAt());
    }
}

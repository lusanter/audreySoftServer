package com.audrey.soft.restaurant.app.usecases.Comanda;

import com.audrey.soft.billing.domain.models.ComprobanteSerie;
import com.audrey.soft.billing.domain.models.Venta;
import com.audrey.soft.billing.domain.models.VentaCobro;
import com.audrey.soft.billing.domain.models.VentaItem;
import com.audrey.soft.billing.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import com.audrey.soft.restaurant.app.dtos.CerrarComandaRequest;
import com.audrey.soft.restaurant.app.dtos.ComandaDTO;
import com.audrey.soft.restaurant.app.mappers.ComandaMapper;
import com.audrey.soft.restaurant.domain.models.EstadoComanda;
import com.audrey.soft.restaurant.domain.models.EstadoItem;
import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.restaurant.domain.ports.ComandaRepositoryPort;
import com.audrey.soft.restaurant.domain.ports.MesaRepositoryPort;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class CerrarComandaUseCase {

    private final ComandaRepositoryPort comandaRepository;
    private final MesaRepositoryPort mesaRepository;
    private final VentaRepositoryPort ventaRepository;
    private final ComprobanteSerieRepositoryPort serieRepository;
    private final ProductoRepositoryPort productoRepository;
    private final StockMovementRepositoryPort stockMovementRepository;
    private final ComandaMapper comandaMapper;

    public CerrarComandaUseCase(ComandaRepositoryPort comandaRepository,
                                MesaRepositoryPort mesaRepository,
                                VentaRepositoryPort ventaRepository,
                                ComprobanteSerieRepositoryPort serieRepository,
                                ProductoRepositoryPort productoRepository,
                                StockMovementRepositoryPort stockMovementRepository,
                                ComandaMapper comandaMapper) {
        this.comandaRepository = comandaRepository;
        this.mesaRepository = mesaRepository;
        this.ventaRepository = ventaRepository;
        this.serieRepository = serieRepository;
        this.productoRepository = productoRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.comandaMapper = comandaMapper;
    }

    @Transactional
    public ComandaDTO execute(UUID comandaId, CerrarComandaRequest request) {
        var comanda = comandaRepository.findById(comandaId)
                .orElseThrow(() -> new RuntimeException("Comanda no encontrada: " + comandaId));

        if (comanda.getEstado() == EstadoComanda.CERRADA)
            throw new IllegalStateException("La comanda ya está cerrada");
        if (comanda.getEstado() == EstadoComanda.CANCELADA)
            throw new IllegalStateException("No se puede cerrar una comanda cancelada");

        // 1. Obtener serie activa y reservar correlativo (con lock para evitar race conditions)
        ComprobanteSerie serie = serieRepository
                .findActivaByTipoAndSucursal(request.tipoComprobante(), comanda.getSucursalId())
                .orElseThrow(() -> new RuntimeException(
                        "No hay serie activa para " + request.tipoComprobante() + " en esta sucursal"));

        int correlativo = serieRepository.incrementarCorrelativo(serie.getId());
        String numeroComprobante = serie.getSerie() + "-" + String.format("%08d", correlativo);

        // 2. Calcular montos
        var itemsActivos = comanda.getItems().stream()
                .filter(i -> i.getEstado() != EstadoItem.CANCELADO).toList();

        BigDecimal subtotal = itemsActivos.stream()
                .map(i -> i.getPrecioUnitario().multiply(i.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = request.descuento() != null ? request.descuento() : BigDecimal.ZERO;
        BigDecimal base = subtotal.subtract(descuento);
        // IGV 18% incluido en precio (precio ya incluye IGV)
        BigDecimal igv = base.multiply(new BigDecimal("0.18"))
                .divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
        BigDecimal total = base;

        // 3. Construir snapshot de items para billing
        List<VentaItem> ventaItems = itemsActivos.stream().map(i -> {
            var producto = productoRepository.findById(i.getProductoId()).orElse(null);
            return new VentaItem(null, null,
                    i.getProductoId(),
                    producto != null ? producto.getNombre() : "Producto eliminado",
                    i.getCantidad(),
                    i.getPrecioUnitario());
        }).toList();

        // 4. Construir cobros
        List<VentaCobro> cobros = request.cobros().stream()
                .map(c -> new VentaCobro(null, null, c.metodoCobro(), c.monto(), c.referencia(), LocalDateTime.now()))
                .toList();

        // 5. Crear venta
        Venta venta = new Venta(null, comanda.getSucursalId(), serie.getId(), comanda.getId(),
                request.clienteId(), request.tipoComprobante(),
                serie.getSerie(), correlativo, numeroComprobante,
                subtotal, descuento, igv, total,
                ventaItems, cobros, LocalDateTime.now());

        ventaRepository.save(venta);

        // 6. Descontar stock y registrar movimiento por cada item (solo si el producto controla stock)
        LocalDateTime ahora = LocalDateTime.now();
        String notaMovimiento = "Venta: " + numeroComprobante;

        itemsActivos.forEach(item -> {
            var producto = productoRepository.findById(item.getProductoId()).orElse(null);
            if (producto != null && producto.isControlStock()) {
                // 6a. Descontar stock
                productoRepository.decrementarStock(item.getProductoId(), item.getCantidad());
                // 6b. Registrar movimiento SALIDA (precioCosto=null — no es una compra)
                stockMovementRepository.save(
                        item.getProductoId(),
                        "SALIDA",
                        item.getCantidad(),
                        null,
                        item.getId(),     // referenciaId
                        null,             // motivoId (null para ventas)
                        notaMovimiento,
                        ahora
                );
            }
        });

        // 7. Cerrar comanda y liberar mesa
        comanda.setEstado(EstadoComanda.CERRADA);
        comanda.setClosedAt(LocalDateTime.now());
        comanda.setTotal(total);
        var comandaCerrada = comandaRepository.save(comanda);

        if (comanda.getMesaId() != null)
            mesaRepository.updateEstado(comanda.getMesaId(), EstadoMesa.LIBRE);

        return comandaMapper.toDto(comandaCerrada);
    }
}

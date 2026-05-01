package com.audrey.soft.restaurant.app.usecases.Comanda;

import com.audrey.soft.billing.domain.models.*;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import com.audrey.soft.fiscal.domain.models.ComprobanteSerie;
import com.audrey.soft.fiscal.domain.models.ImpuestoCalculator;
import com.audrey.soft.fiscal.domain.models.NumeroComprobanteFormatterFactory;
import com.audrey.soft.fiscal.domain.models.VentaImpuesto;
import com.audrey.soft.fiscal.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.fiscal.infrastructure.persistence.entities.ImpuestoTipoEntity;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalConfigRepository;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataImpuestoTipoRepository;
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
import java.time.LocalDateTime;
import java.util.Arrays;
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
    private final SpringDataFiscalConfigRepository fiscalConfigRepository;
    private final SpringDataImpuestoTipoRepository impuestoTipoRepository;

    public CerrarComandaUseCase(ComandaRepositoryPort comandaRepository,
                                MesaRepositoryPort mesaRepository,
                                VentaRepositoryPort ventaRepository,
                                ComprobanteSerieRepositoryPort serieRepository,
                                ProductoRepositoryPort productoRepository,
                                StockMovementRepositoryPort stockMovementRepository,
                                ComandaMapper comandaMapper,
                                SpringDataFiscalConfigRepository fiscalConfigRepository,
                                SpringDataImpuestoTipoRepository impuestoTipoRepository) {
        this.comandaRepository = comandaRepository;
        this.mesaRepository = mesaRepository;
        this.ventaRepository = ventaRepository;
        this.serieRepository = serieRepository;
        this.productoRepository = productoRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.comandaMapper = comandaMapper;
        this.fiscalConfigRepository = fiscalConfigRepository;
        this.impuestoTipoRepository = impuestoTipoRepository;
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

        // 2. Leer fiscal config de la sucursal
        var fiscalConfig = fiscalConfigRepository.findBySucursalId(comanda.getSucursalId()).orElse(null);
        String fiscalSistemaId = fiscalConfig != null ? fiscalConfig.getFiscalSistemaId() : null;
        boolean preciosIncluyenImpuesto = fiscalConfig == null || fiscalConfig.isPreciosIncluyenImpuesto();

        // Cargar tipos de impuesto
        List<ImpuestoTipoEntity> impuestoTipos = List.of();
        if (fiscalConfig != null && fiscalConfig.getImpuestosDefault() != null && fiscalConfig.getImpuestosDefault().length > 0) {
            impuestoTipos = impuestoTipoRepository.findAllByIdIn(Arrays.asList(fiscalConfig.getImpuestosDefault()));
        }

        // Generar número de comprobante
        String numeroComprobante;
        if (fiscalSistemaId != null && !"INTERNO".equals(fiscalSistemaId)) {
            var formatter = NumeroComprobanteFormatterFactory.forSistema(fiscalSistemaId);
            numeroComprobante = formatter.format(serie.getSerie(), correlativo);
        } else {
            numeroComprobante = serie.getSerie() + "-" + String.format("%08d", correlativo);
        }

        // 3. Calcular montos
        var itemsActivos = comanda.getItems().stream()
                .filter(i -> i.getEstado() != EstadoItem.CANCELADO).toList();

        BigDecimal subtotal = itemsActivos.stream()
                .map(i -> i.getPrecioUnitario().multiply(i.getCantidad()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal descuento = request.descuento() != null ? request.descuento() : BigDecimal.ZERO;
        BigDecimal base = subtotal.subtract(descuento);

        // Calcular impuestos usando ImpuestoCalculator
        List<VentaImpuesto> impuestos = ImpuestoCalculator.calcular(base, impuestoTipos, preciosIncluyenImpuesto);

        BigDecimal totalImpuestos = impuestos.stream()
                .map(VentaImpuesto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal total = preciosIncluyenImpuesto ? base : base.add(totalImpuestos);

        // 4. Construir snapshot de items para billing
        List<VentaItem> ventaItems = itemsActivos.stream().map(i -> {
            var producto = productoRepository.findById(i.getProductoId()).orElse(null);
            return new VentaItem(null, null,
                    i.getProductoId(),
                    producto != null ? producto.getNombre() : "Producto eliminado",
                    i.getCantidad(),
                    i.getPrecioUnitario());
        }).toList();

        // 5. Construir cobros
        List<VentaCobro> cobros = request.cobros().stream()
                .map(c -> new VentaCobro(null, null, c.metodoCobro(), c.nombre(), c.monto(), c.referencia(), LocalDateTime.now()))
                .toList();

        // 6. Crear venta
        Venta venta = new Venta(null, comanda.getSucursalId(), serie.getId(),
                new VentaOrigen(TipoOrigen.COMANDA.name(), comanda.getId()),
                request.clienteId(), request.tipoComprobante(),
                serie.getSerie(), correlativo, numeroComprobante,
                subtotal, descuento, base, totalImpuestos, total, "COBRADA", false, fiscalSistemaId,
                ventaItems, cobros, impuestos, LocalDateTime.now());

        ventaRepository.save(venta);

        // 7. Descontar stock y registrar movimiento por cada item (solo si el producto controla stock)
        LocalDateTime ahora = LocalDateTime.now();
        String notaMovimiento = "Venta: " + numeroComprobante;

        itemsActivos.forEach(item -> {
            var producto = productoRepository.findById(item.getProductoId()).orElse(null);
            if (producto != null && producto.isControlStock()) {
                // 7a. Descontar stock
                productoRepository.decrementarStock(item.getProductoId(), item.getCantidad());
                // 7b. Registrar movimiento SALIDA (precioCosto=null — no es una compra)
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

        // 8. Cerrar comanda y liberar mesa
        comanda.setEstado(EstadoComanda.CERRADA);
        comanda.setClosedAt(LocalDateTime.now());
        comanda.setTotal(total);
        var comandaCerrada = comandaRepository.save(comanda);

        if (comanda.getMesaId() != null)
            mesaRepository.updateEstado(comanda.getMesaId(), EstadoMesa.LIBRE);

        return comandaMapper.toDto(comandaCerrada);
    }
}

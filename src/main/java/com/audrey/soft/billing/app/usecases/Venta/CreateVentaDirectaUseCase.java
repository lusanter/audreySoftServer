package com.audrey.soft.billing.app.usecases.Venta;

import com.audrey.soft.billing.app.dtos.CreateVentaDirectaRequest;
import com.audrey.soft.billing.app.dtos.VentaCobroDTO;
import com.audrey.soft.billing.app.dtos.VentaDTO;
import com.audrey.soft.billing.app.dtos.VentaItemDTO;
import com.audrey.soft.billing.domain.models.*;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import com.audrey.soft.fiscal.app.dtos.VentaImpuestoDTO;
import com.audrey.soft.fiscal.domain.models.ImpuestoCalculator;
import com.audrey.soft.fiscal.domain.models.NumeroComprobanteFormatterFactory;
import com.audrey.soft.fiscal.domain.models.VentaImpuesto;
import com.audrey.soft.fiscal.domain.ports.ComprobanteSerieRepositoryPort;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataFiscalConfigRepository;
import com.audrey.soft.fiscal.infrastructure.persistence.repositories.SpringDataImpuestoTipoRepository;
import com.audrey.soft.inventory.domain.ports.ProductoRepositoryPort;
import com.audrey.soft.inventory.domain.ports.StockMovementRepositoryPort;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CreateVentaDirectaUseCase {

    private final VentaRepositoryPort ventaRepository;
    private final ComprobanteSerieRepositoryPort serieRepository;
    private final ProductoRepositoryPort productoRepository;
    private final StockMovementRepositoryPort stockMovementRepository;
    private final SpringDataFiscalConfigRepository fiscalConfigRepository;
    private final SpringDataImpuestoTipoRepository impuestoTipoRepository;

    public CreateVentaDirectaUseCase(VentaRepositoryPort ventaRepository,
                                     ComprobanteSerieRepositoryPort serieRepository,
                                     ProductoRepositoryPort productoRepository,
                                     StockMovementRepositoryPort stockMovementRepository,
                                     SpringDataFiscalConfigRepository fiscalConfigRepository,
                                     SpringDataImpuestoTipoRepository impuestoTipoRepository) {
        this.ventaRepository = ventaRepository;
        this.serieRepository = serieRepository;
        this.productoRepository = productoRepository;
        this.stockMovementRepository = stockMovementRepository;
        this.fiscalConfigRepository = fiscalConfigRepository;
        this.impuestoTipoRepository = impuestoTipoRepository;
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

        // Leer configuración fiscal de la sucursal
        var fiscalConfig = fiscalConfigRepository.findBySucursalId(sucursalId).orElse(null);
        String fiscalSistemaId = fiscalConfig != null ? fiscalConfig.getFiscalSistemaId() : null;
        boolean preciosIncluyenImpuesto = fiscalConfig == null || fiscalConfig.isPreciosIncluyenImpuesto();

        // Cargar tipos de impuesto
        var impuestoTipos = List.<com.audrey.soft.fiscal.infrastructure.persistence.entities.ImpuestoTipoEntity>of();
        if (fiscalConfig != null && fiscalConfig.getImpuestosDefault() != null && fiscalConfig.getImpuestosDefault().length > 0) {
            impuestoTipos = impuestoTipoRepository.findAllByIdIn(Arrays.asList(fiscalConfig.getImpuestosDefault()));
        }

        // Calcular base e impuestos
        BigDecimal base = subtotal.subtract(descuento);
        List<VentaImpuesto> impuestos = ImpuestoCalculator.calcular(base, impuestoTipos, preciosIncluyenImpuesto);

        BigDecimal totalImpuestos = impuestos.stream()
                .map(VentaImpuesto::getMonto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Total: si los precios incluyen impuesto, total = base; si no, total = base + totalImpuestos
        BigDecimal total = preciosIncluyenImpuesto ? base : base.add(totalImpuestos);

        // Obtener serie activa y reservar correlativo
        String tipoComp = req.tipoComprobante() != null ? req.tipoComprobante() : "NOTA_VENTA";
        var serie = serieRepository.findActivaByTipoAndSucursal(tipoComp, sucursalId)
                .orElseThrow(() -> new IllegalStateException(
                        "No hay serie activa para " + tipoComp + " en esta sucursal. Configúrala en Inventario > Configuración > Comprobantes."));

        int correlativo = serieRepository.incrementarCorrelativo(serie.getId());
        String numeroComprobante;
        if (fiscalSistemaId != null && !"INTERNO".equals(fiscalSistemaId)) {
            var formatter = NumeroComprobanteFormatterFactory.forSistema(fiscalSistemaId);
            numeroComprobante = formatter.format(serie.getSerie(), correlativo);
        } else {
            // Fallback para INTERNO o sin configuración fiscal: formato SUNAT
            numeroComprobante = serie.getSerie() + "-" + String.format("%08d", correlativo);
        }

        // Construir items
        List<VentaItem> items = req.items().stream().map(i ->
                new VentaItem(null, null, i.productoId(), i.nombreProducto(),
                        i.cantidad(), i.precioUnitario())
        ).toList();

        // Construir cobros
        List<VentaCobro> cobros = req.cobros().stream().map(c ->
                new VentaCobro(null, null, c.metodoCobro(), c.nombre(), c.monto(), c.referencia(), LocalDateTime.now())
        ).toList();

        // Origen DIRECTO — el adapter reemplaza origenId con el id de la venta generada
        Venta venta = new Venta(
                null, sucursalId,
                serie.getId(),
                new VentaOrigen(TipoOrigen.DIRECTO.name(), UUID.randomUUID()),
                req.clienteId(),
                tipoComp,
                serie.getSerie(), correlativo, numeroComprobante,
                subtotal, descuento, base, totalImpuestos, total,
                "COBRADA", false, fiscalSistemaId,
                items, cobros, impuestos,
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
                new VentaCobroDTO(c.getId(), c.getMetodoCobro(), c.getNombreMetodoCobro(),
                        c.getMonto(), c.getReferencia())).toList();
        List<VentaImpuesto> savedImpuestos = saved.getImpuestos() != null ? saved.getImpuestos() : List.of();
        var impuestosDTO = savedImpuestos.stream()
                .map(i -> new VentaImpuestoDTO(i.getCodigo(), i.getNombre(), i.getTasa(), i.getMonto()))
                .toList();

        return new VentaDTO(saved.getId(), saved.getSucursalId(),
                null, null,
                saved.getClienteId(),
                saved.getTipoComprobante(), saved.getSerie(), saved.getCorrelativo(), saved.getNumeroComprobante(),
                saved.getSubtotal(), saved.getDescuento(), saved.getTotalImpuestos(), saved.getTotal(),
                "COBRADA", saved.isFiscalEnviado(), saved.getFiscalSistemaId(), impuestosDTO, itemsDTO, cobrosDTO, saved.getCreatedAt());
    }
}

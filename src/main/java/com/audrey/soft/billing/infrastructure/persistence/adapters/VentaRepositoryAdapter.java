package com.audrey.soft.billing.infrastructure.persistence.adapters;

import com.audrey.soft.billing.domain.models.Venta;
import com.audrey.soft.billing.domain.models.VentaCobro;
import com.audrey.soft.billing.domain.models.VentaItem;
import com.audrey.soft.billing.domain.models.VentaOrigen;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;
import com.audrey.soft.billing.infrastructure.persistence.entities.ComprobanteSerieEntity;
import com.audrey.soft.billing.infrastructure.persistence.entities.VentaCobroEntity;
import com.audrey.soft.billing.infrastructure.persistence.entities.VentaEntity;
import com.audrey.soft.billing.infrastructure.persistence.entities.VentaItemEntity;
import com.audrey.soft.billing.infrastructure.persistence.entities.VentaOrigenEntity;
import com.audrey.soft.billing.infrastructure.persistence.repositories.SpringDataComprobanteSerieRepository;
import com.audrey.soft.billing.infrastructure.persistence.repositories.SpringDataVentaOrigenRepository;
import com.audrey.soft.billing.infrastructure.persistence.repositories.SpringDataVentaRepository;
import com.audrey.soft.tenant.infrastructure.persistence.repositories.SpringDataSucursalRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class VentaRepositoryAdapter implements VentaRepositoryPort {

    private final SpringDataVentaRepository jpa;
    private final SpringDataSucursalRepository sucursalJpa;
    private final SpringDataComprobanteSerieRepository serieJpa;
    private final SpringDataVentaOrigenRepository ventaOrigenJpa;

    public VentaRepositoryAdapter(SpringDataVentaRepository jpa,
                                  SpringDataSucursalRepository sucursalJpa,
                                  SpringDataComprobanteSerieRepository serieJpa,
                                  SpringDataVentaOrigenRepository ventaOrigenJpa) {
        this.jpa = jpa;
        this.sucursalJpa = sucursalJpa;
        this.serieJpa = serieJpa;
        this.ventaOrigenJpa = ventaOrigenJpa;
    }

    @Override
    public Venta save(Venta venta) {
        var sucursal = sucursalJpa.findById(venta.getSucursalId())
                .orElseThrow(() -> new RuntimeException("Sucursal no encontrada: " + venta.getSucursalId()));

        ComprobanteSerieEntity serie = venta.getComprobanteSerieId() != null
                ? serieJpa.findById(venta.getComprobanteSerieId()).orElse(null)
                : null;

        VentaEntity entity = VentaEntity.builder()
                .sucursal(sucursal)
                .comprobanteSerie(serie)
                .clienteId(venta.getClienteId())
                .tipoComprobante(venta.getTipoComprobante())
                .serie(venta.getSerie())
                .correlativo(venta.getCorrelativo())
                .numeroComprobante(venta.getNumeroComprobante())
                .subtotal(venta.getSubtotal())
                .descuento(venta.getDescuento())
                .igv(venta.getIgv())
                .total(venta.getTotal())
                .build();

        // Mapear items
        if (venta.getItems() != null) {
            List<VentaItemEntity> items = venta.getItems().stream().map(i ->
                    VentaItemEntity.builder()
                            .venta(entity)
                            .productoId(i.getProductoId())
                            .nombreProducto(i.getNombreProducto())
                            .cantidad(i.getCantidad())
                            .precioUnitario(i.getPrecioUnitario())
                            .build()
            ).toList();
            entity.setItems(items);
        }

        // Mapear cobros
        if (venta.getCobros() != null) {
            List<VentaCobroEntity> cobros = venta.getCobros().stream().map(c ->
                    VentaCobroEntity.builder()
                            .venta(entity)
                            .metodoCobro(c.getMetodoCobro())
                            .monto(c.getMonto())
                            .referencia(c.getReferencia())
                            .build()
            ).toList();
            entity.setCobros(cobros);
        }

        VentaEntity saved = jpa.save(entity);

        if (venta.getOrigen() != null) {
            VentaOrigenEntity origenEntity = VentaOrigenEntity.builder()
                    .venta(saved)
                    .tipoOrigen(venta.getOrigen().getTipoOrigen())
                    .origenId(venta.getOrigen().getOrigenId())
                    .build();
            ventaOrigenJpa.save(origenEntity);
        }

        return toDomain(saved);
    }

    @Override
    public Optional<Venta> findById(UUID id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<Venta> findBySucursalId(UUID sucursalId) {
        return jpa.findBySucursalId(sucursalId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Venta> findByFiltro(UUID sucursalId, com.audrey.soft.billing.app.dtos.VentaFiltroDTO filtro) {
        var desde = filtro.desde().atStartOfDay();
        var hasta = filtro.hasta().atTime(23, 59, 59, 999_999_999);
        return jpa.findByFiltro(sucursalId, desde, hasta,
                filtro.estado(), filtro.tipoComprobante(), filtro.serie(), filtro.sunatEnviado())
                .stream().map(this::toDomain).toList();
    }

    private Venta toDomain(VentaEntity e) {
        List<VentaItem> items = e.getItems() != null ? e.getItems().stream().map(i ->
                new VentaItem(i.getId(), e.getId(), i.getProductoId(),
                        i.getNombreProducto(), i.getCantidad(), i.getPrecioUnitario())
        ).toList() : List.of();

        List<VentaCobro> cobros = e.getCobros() != null ? e.getCobros().stream().map(c ->
                new VentaCobro(c.getId(), e.getId(), c.getMetodoCobro(),
                        c.getMonto(), c.getReferencia(), c.getCreatedAt())
        ).toList() : List.of();

        return new Venta(e.getId(), e.getSucursal().getId(),
                e.getComprobanteSerie() != null ? e.getComprobanteSerie().getId() : null,
                e.getOrigen() != null
                        ? new VentaOrigen(e.getOrigen().getTipoOrigen(), e.getOrigen().getOrigenId())
                        : null,
                e.getClienteId(), e.getTipoComprobante(),
                e.getSerie(), e.getCorrelativo(), e.getNumeroComprobante(),
                e.getSubtotal(), e.getDescuento(), e.getIgv(), e.getTotal(),
                items, cobros, e.getCreatedAt());
    }
}

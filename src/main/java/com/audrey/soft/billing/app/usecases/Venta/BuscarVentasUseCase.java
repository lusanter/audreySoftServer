package com.audrey.soft.billing.app.usecases.Venta;

import com.audrey.soft.billing.app.dtos.VentaCobroDTO;
import com.audrey.soft.billing.app.dtos.VentaDTO;
import com.audrey.soft.billing.app.dtos.VentaFiltroDTO;
import com.audrey.soft.billing.app.dtos.VentaItemDTO;
import com.audrey.soft.billing.domain.ports.VentaRepositoryPort;

import java.util.List;
import java.util.UUID;

public class BuscarVentasUseCase {
    private final VentaRepositoryPort repo;

    public BuscarVentasUseCase(VentaRepositoryPort repo) {
        this.repo = repo;
    }

    public List<VentaDTO> execute(UUID sucursalId, VentaFiltroDTO filtro) {
        return repo.findByFiltro(sucursalId, filtro).stream().map(v -> {
            var items = v.getItems().stream().map(i ->
                    new VentaItemDTO(i.getId(), i.getProductoId(), i.getNombreProducto(),
                            i.getCantidad(), i.getPrecioUnitario())).toList();
            var cobros = v.getCobros().stream().map(c ->
                    new VentaCobroDTO(c.getId(), c.getMetodoCobro(), c.getMonto(), c.getReferencia())).toList();
            return new VentaDTO(v.getId(), v.getSucursalId(),
                    v.getOrigen() != null ? v.getOrigen().getTipoOrigen() : null,
                    v.getOrigen() != null ? v.getOrigen().getOrigenId()   : null,
                    v.getClienteId(),
                    v.getTipoComprobante(), v.getSerie(), v.getCorrelativo(), v.getNumeroComprobante(),
                    v.getSubtotal(), v.getDescuento(), v.getIgv(), v.getTotal(),
                    "COBRADA", false, items, cobros, v.getCreatedAt());
        }).toList();
    }
}

package com.audrey.soft.billing.domain.ports;

import com.audrey.soft.billing.app.dtos.VentaFiltroDTO;
import com.audrey.soft.billing.domain.models.Venta;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VentaRepositoryPort {
    Venta save(Venta venta);
    Optional<Venta> findById(UUID id);
    List<Venta> findBySucursalId(UUID sucursalId);
    List<Venta> findByFiltro(UUID sucursalId, VentaFiltroDTO filtro);
}

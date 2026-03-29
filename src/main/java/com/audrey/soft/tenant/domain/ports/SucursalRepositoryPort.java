package com.audrey.soft.tenant.domain.ports;

import com.audrey.soft.tenant.domain.models.Sucursal;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SucursalRepositoryPort {
    Sucursal save(Sucursal sucursal);
    Optional<Sucursal> findById(UUID id);
    List<Sucursal> findByEmpresaId(UUID empresaId);
    boolean existsByNombreAndEmpresaId(String nombre, UUID empresaId);
}

package com.audrey.soft.tenant.app.usecases.Sucursal;

import com.audrey.soft.tenant.domain.models.Sucursal;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;

import java.util.UUID;

public class ToggleBranchStatusUseCase {

    private final SucursalRepositoryPort sucursalRepositoryPort;

    public ToggleBranchStatusUseCase(SucursalRepositoryPort sucursalRepositoryPort) {
        this.sucursalRepositoryPort = sucursalRepositoryPort;
    }

    public void execute(UUID sucursalId, boolean activate) {
        Sucursal sucursal = sucursalRepositoryPort.findById(sucursalId)
                .orElseThrow(() -> new RuntimeException("Sucursal inexistente con ID: " + sucursalId));
        sucursal.setActive(activate);
        sucursalRepositoryPort.save(sucursal);
    }
}

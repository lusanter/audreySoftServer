package com.audrey.soft.tenant.app.usecases.Sucursal;

import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.app.mappers.SucursalMapper;
import com.audrey.soft.tenant.domain.models.Sucursal;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;

import java.util.UUID;


public class UpdateBranchUseCase {
    private final SucursalRepositoryPort sucursalRepositoryPort;
    private final SucursalMapper sucursalMapper;


    public UpdateBranchUseCase(SucursalRepositoryPort sucursalRepositoryPort, SucursalMapper sucursalMapper) {
        this.sucursalMapper = sucursalMapper;
        this.sucursalRepositoryPort = sucursalRepositoryPort;
    }

    public SucursalDTO execute(UUID id, SucursalDTO request) {

        Sucursal sucursal = sucursalRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Sucursal inexistente con ID: " + id));

        sucursal.setNombre(request.nombre());
        sucursal.setDireccion(request.direccion());
        sucursal.setImagenUrl(request.imagenUrl());
        sucursal.setActive(request.active());

        Sucursal guardada = sucursalRepositoryPort.save(sucursal);

        return new SucursalDTO(
                guardada.getId(),
                guardada.getEmpresaId(),
                guardada.getNombre(),
                guardada.getDireccion(),
                guardada.getImagenUrl(),
                guardada.getVertical(),
                guardada.isActive(),
                guardada.getPaisCodigo(),
                guardada.getMonedaCodigo(),
                guardada.getCreatedAt()
                );
    }
}

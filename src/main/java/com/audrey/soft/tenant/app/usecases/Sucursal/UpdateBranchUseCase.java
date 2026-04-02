package com.audrey.soft.tenant.app.usecases.Sucursal;

import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.app.mappers.SucursalMapper;
import com.audrey.soft.tenant.domain.models.Sucursal;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;


public class UpdateBranchUseCase {
    private final SucursalRepositoryPort sucursalRepositoryPort;
    private final SucursalMapper sucursalMapper;


    public UpdateBranchUseCase(SucursalRepositoryPort sucursalRepositoryPort, SucursalMapper sucursalMapper) {
        this.sucursalMapper = sucursalMapper;
        this.sucursalRepositoryPort = sucursalRepositoryPort;
    }

    public SucursalDTO execute(SucursalDTO request) {
        Sucursal sucursal = sucursalMapper.toDomain(request);
        Sucursal guardada = sucursalRepositoryPort.save(sucursal);
        return sucursalMapper.toDto(guardada);
    }

}

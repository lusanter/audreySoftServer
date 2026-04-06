package com.audrey.soft.tenant.app.usecases.Sucursal;

import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.app.mappers.SucursalMapper;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;

import java.util.UUID;

public class GetBranchByIdUseCase {

    private final SucursalRepositoryPort sucursalRepository;
    private final SucursalMapper sucursalMapper;

    public GetBranchByIdUseCase(SucursalRepositoryPort sucursalRepository, SucursalMapper sucursalMapper) {
        this.sucursalRepository = sucursalRepository;
        this.sucursalMapper = sucursalMapper;
    }

    public SucursalDTO execute(UUID id) {
        return sucursalRepository.findById(id)
                .map(sucursalMapper::toDto)
                .orElseThrow(() -> new IllegalArgumentException("Sucursal no encontrada: " + id));
    }
}

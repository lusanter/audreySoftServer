package com.audrey.soft.tenant.app.usecases.Sucursal;

import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.app.mappers.SucursalMapper;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;

import java.util.List;
import java.util.UUID;

public class ListBranchesUseCase {

    private final SucursalRepositoryPort sucursalRepository;
    private final SucursalMapper sucursalMapper;

    public ListBranchesUseCase(SucursalRepositoryPort sucursalRepository,
                               SucursalMapper sucursalMapper) {
        this.sucursalRepository = sucursalRepository;
        this.sucursalMapper = sucursalMapper;
    }

    public List<SucursalDTO> execute() {
        return sucursalRepository.getAll().stream().map(sucursalMapper::toDto).toList();
    }

    public List<SucursalDTO> executeById(UUID empresaId) {
        return sucursalRepository.findByEmpresaId(empresaId).stream()
                .map(sucursalMapper::toDto)
                .toList();
    }
}

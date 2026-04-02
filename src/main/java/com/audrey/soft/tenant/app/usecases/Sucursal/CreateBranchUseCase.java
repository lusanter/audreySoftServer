package com.audrey.soft.tenant.app.usecases.Sucursal;

import com.audrey.soft.tenant.app.dtos.SucursalDTO;
import com.audrey.soft.tenant.app.mappers.SucursalMapper;
import com.audrey.soft.tenant.domain.models.Sucursal;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;
import com.audrey.soft.tenant.domain.ports.SucursalRepositoryPort;

import java.util.UUID;

public class CreateBranchUseCase {

    private final SucursalRepositoryPort sucursalRepository;
    private final EmpresaRepositoryPort empresaRepository;
    private final SucursalMapper sucursalMapper;

    public CreateBranchUseCase(SucursalRepositoryPort sucursalRepository,
                                EmpresaRepositoryPort empresaRepository,
                                SucursalMapper sucursalMapper) {
        this.sucursalRepository = sucursalRepository;
        this.empresaRepository = empresaRepository;
        this.sucursalMapper = sucursalMapper;
    }

    public SucursalDTO execute(UUID empresaId, SucursalDTO request) {
        empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada: " + empresaId));

        if (sucursalRepository.existsByNombreAndEmpresaId(request.nombre(), empresaId)) {
            throw new IllegalArgumentException(
                    "Ya existe una sucursal con el nombre '" + request.nombre() + "' en esta empresa");
        }

        Sucursal nueva = new Sucursal(null, empresaId, request.nombre(),
                request.direccion(), request.vertical(), true, null, null);
        Sucursal guardada = sucursalRepository.save(nueva);
        return sucursalMapper.toDto(guardada);
    }
}

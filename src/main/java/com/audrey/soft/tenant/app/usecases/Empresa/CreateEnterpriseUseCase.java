package com.audrey.soft.tenant.app.usecases.Empresa;

import com.audrey.soft.tenant.app.dtos.EmpresaDTO;
import com.audrey.soft.tenant.app.mappers.EmpresaMapper;
import com.audrey.soft.tenant.domain.models.Empresa;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;

public class CreateEnterpriseUseCase {

    private final EmpresaRepositoryPort empresaRepository;
    private final EmpresaMapper empresaMapper;

    public CreateEnterpriseUseCase(EmpresaRepositoryPort empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    public EmpresaDTO execute(EmpresaDTO request) {
        if (empresaRepository.existsByRuc(request.ruc())) {
            throw new IllegalArgumentException("Ya existe una empresa con el RUC: " + request.ruc());
        }

        Empresa nueva = new Empresa(null, request.nombre(), request.ruc(), true, null, null);
        Empresa guardada = empresaRepository.save(nueva);
        return empresaMapper.toDto(guardada);
    }
}

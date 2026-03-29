package com.audrey.soft.tenant.app.usecases;

import com.audrey.soft.tenant.app.dtos.EmpresaDTO;
import com.audrey.soft.tenant.app.mappers.EmpresaMapper;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;

import java.util.List;

public class ListEnterprisesUseCase {

    private final EmpresaRepositoryPort empresaRepository;
    private final EmpresaMapper empresaMapper;

    public ListEnterprisesUseCase(EmpresaRepositoryPort empresaRepository, EmpresaMapper empresaMapper) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    public List<EmpresaDTO> execute() {
        return empresaRepository.findAll().stream()
                .map(empresaMapper::toDto)
                .toList();
    }
}

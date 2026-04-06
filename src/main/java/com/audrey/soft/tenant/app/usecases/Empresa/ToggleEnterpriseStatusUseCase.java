package com.audrey.soft.tenant.app.usecases.Empresa;

import com.audrey.soft.tenant.domain.models.Empresa;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;

import java.util.UUID;

public class ToggleEnterpriseStatusUseCase {

    private final EmpresaRepositoryPort empresaRepositoryPort;

    public ToggleEnterpriseStatusUseCase(EmpresaRepositoryPort empresaRepositoryPort) {
        this.empresaRepositoryPort = empresaRepositoryPort;
    }

    public void execute(UUID empresaId, boolean activate) {
        Empresa empresa = empresaRepositoryPort.findById(empresaId)
                .orElseThrow(() -> new RuntimeException("Empresa inexistente con ID: " + empresaId));
        empresa.setActive(activate);
        empresaRepositoryPort.save(empresa);
    }
}

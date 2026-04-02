package com.audrey.soft.tenant.app.usecases.Empresa;

import com.audrey.soft.tenant.app.dtos.EmpresaDTO;
import com.audrey.soft.tenant.domain.models.Empresa;
import com.audrey.soft.tenant.domain.ports.EmpresaRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UpdateEnterpriseUseCase {

    private final EmpresaRepositoryPort empresaRepositoryPort;

    public UpdateEnterpriseUseCase(EmpresaRepositoryPort empresaRepositoryPort) {
        this.empresaRepositoryPort = empresaRepositoryPort;
    }

    @Transactional
    public EmpresaDTO execute(UUID id, EmpresaDTO payload) {
        // Encontrar ADN del Inquilino o Fallar
        Empresa empresa = empresaRepositoryPort.findById(id)
                .orElseThrow(() -> new RuntimeException("Inquilino inexistente con ID: " + id));

        // Validación Cruzada: Si el cliente está tratando de ponerse el RUC de otro SaaS activo, abortar
        if (!empresa.getRuc().equals(payload.ruc()) && empresaRepositoryPort.existsByRuc(payload.ruc())) {
            throw new RuntimeException("El RUC " + payload.ruc() + " le pertenece a otra entidad B2B activa");
        }

        // Aplicamos mutaciones directas al Dominio Aislado
        empresa.setNombre(payload.nombre());
        empresa.setRuc(payload.ruc());
        empresa.setActive(payload.active());
        empresa.setUpdatedAt(LocalDateTime.now());

        // El Repositorio detecta que tiene ID, por ende lanza una sentencia UPDATE automática
        Empresa guardada = empresaRepositoryPort.save(empresa);

        // Retornamos la Copia Visual Hidratada al Frontend
        return new EmpresaDTO(
                guardada.getId(),
                guardada.getNombre(),
                guardada.getRuc(),
                guardada.isActive(),
                guardada.getCreatedAt()
        );
    }
}

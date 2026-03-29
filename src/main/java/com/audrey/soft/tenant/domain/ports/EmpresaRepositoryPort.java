package com.audrey.soft.tenant.domain.ports;

import com.audrey.soft.tenant.domain.models.Empresa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EmpresaRepositoryPort {
    Empresa save(Empresa empresa);
    Optional<Empresa> findById(UUID id);
    List<Empresa> findAll();
    boolean existsByRuc(String ruc);
}

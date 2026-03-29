package com.audrey.soft.tenant.infrastructure.persistence.repositories;

import com.audrey.soft.tenant.infrastructure.persistence.entities.SucursalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataSucursalRepository extends JpaRepository<SucursalEntity, UUID> {
    List<SucursalEntity> findByEmpresaId(UUID empresaId);
    boolean existsByNombreAndEmpresaId(String nombre, UUID empresaId);
}

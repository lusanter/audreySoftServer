package com.audrey.soft.tenant.infrastructure.persistence.repositories;

import com.audrey.soft.tenant.infrastructure.persistence.entities.EmpresaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataEmpresaRepository extends JpaRepository<EmpresaEntity, UUID> {
    boolean existsByRuc(String ruc);
}

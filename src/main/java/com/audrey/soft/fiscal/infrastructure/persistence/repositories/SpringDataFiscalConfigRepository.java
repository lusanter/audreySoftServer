package com.audrey.soft.fiscal.infrastructure.persistence.repositories;

import com.audrey.soft.fiscal.infrastructure.persistence.entities.FiscalConfigEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface SpringDataFiscalConfigRepository extends JpaRepository<FiscalConfigEntity, UUID> {

    Optional<FiscalConfigEntity> findBySucursalId(UUID sucursalId);
}

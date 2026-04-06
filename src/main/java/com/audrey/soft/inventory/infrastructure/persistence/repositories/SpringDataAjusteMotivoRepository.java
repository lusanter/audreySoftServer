package com.audrey.soft.inventory.infrastructure.persistence.repositories;

import com.audrey.soft.inventory.infrastructure.persistence.entities.AjusteMotivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface SpringDataAjusteMotivoRepository extends JpaRepository<AjusteMotivoEntity, UUID> {
    List<AjusteMotivoEntity> findBySucursalIdAndActiveTrue(UUID sucursalId);
}

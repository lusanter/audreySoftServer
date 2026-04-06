package com.audrey.soft.billing.infrastructure.persistence.repositories;

import com.audrey.soft.billing.infrastructure.persistence.entities.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataVentaRepository extends JpaRepository<VentaEntity, UUID> {
    List<VentaEntity> findBySucursalId(UUID sucursalId);
}

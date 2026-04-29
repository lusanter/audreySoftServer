package com.audrey.soft.billing.infrastructure.persistence.repositories;

import com.audrey.soft.billing.infrastructure.persistence.entities.VentaOrigenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataVentaOrigenRepository extends JpaRepository<VentaOrigenEntity, UUID> {
}

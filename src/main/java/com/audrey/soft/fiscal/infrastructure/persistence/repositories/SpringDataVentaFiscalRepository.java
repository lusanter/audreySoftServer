package com.audrey.soft.fiscal.infrastructure.persistence.repositories;

import com.audrey.soft.fiscal.infrastructure.persistence.entities.VentaFiscalEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataVentaFiscalRepository extends JpaRepository<VentaFiscalEntity, UUID> {
}

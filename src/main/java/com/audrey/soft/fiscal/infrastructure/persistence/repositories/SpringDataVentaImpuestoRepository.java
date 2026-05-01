package com.audrey.soft.fiscal.infrastructure.persistence.repositories;

import com.audrey.soft.fiscal.infrastructure.persistence.entities.VentaImpuestoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataVentaImpuestoRepository extends JpaRepository<VentaImpuestoEntity, UUID> {

    List<VentaImpuestoEntity> findAllByVentaId(UUID ventaId);
}

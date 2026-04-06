package com.audrey.soft.billing.infrastructure.persistence.repositories;

import com.audrey.soft.billing.infrastructure.persistence.entities.MetodoCobroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataMetodoCobroRepository extends JpaRepository<MetodoCobroEntity, UUID> {

    // Devuelve globales (sucursal_id IS NULL) + los específicos de la sucursal
    @Query("SELECT m FROM MetodoCobroEntity m WHERE m.activo = true AND (m.sucursalId IS NULL OR m.sucursalId = :sucursalId)")
    List<MetodoCobroEntity> findDisponiblesBySucursal(@Param("sucursalId") UUID sucursalId);
}

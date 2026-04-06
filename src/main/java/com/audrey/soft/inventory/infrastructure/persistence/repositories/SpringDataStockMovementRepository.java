package com.audrey.soft.inventory.infrastructure.persistence.repositories;

import com.audrey.soft.inventory.infrastructure.persistence.entities.StockMovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataStockMovementRepository extends JpaRepository<StockMovementEntity, UUID> {

    @Query("SELECT sm FROM StockMovementEntity sm JOIN sm.producto p " +
           "WHERE p.sucursal.id = :sucursalId ORDER BY sm.createdAt DESC")
    List<StockMovementEntity> findBySucursalId(@Param("sucursalId") UUID sucursalId);
}

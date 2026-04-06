package com.audrey.soft.restaurant.infrastructure.persistence.repositories;

import com.audrey.soft.restaurant.domain.models.EstadoComanda;
import com.audrey.soft.restaurant.infrastructure.persistence.entities.ComandaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataComandaRepository extends JpaRepository<ComandaEntity, UUID> {
    List<ComandaEntity> findBySucursalId(UUID sucursalId);
    List<ComandaEntity> findBySucursalIdAndEstado(UUID sucursalId, EstadoComanda estado);

    @Query("SELECT c FROM ComandaEntity c WHERE c.mesaId = :mesaId AND c.estado NOT IN ('CERRADA','CANCELADA')")
    Optional<ComandaEntity> findOpenByMesaId(@Param("mesaId") UUID mesaId);
}

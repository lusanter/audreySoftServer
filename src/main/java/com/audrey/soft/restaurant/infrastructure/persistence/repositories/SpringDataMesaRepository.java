package com.audrey.soft.restaurant.infrastructure.persistence.repositories;

import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.restaurant.infrastructure.persistence.entities.MesaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataMesaRepository extends JpaRepository<MesaEntity, UUID> {
    List<MesaEntity> findBySucursalId(UUID sucursalId);
    boolean existsByNumeroAndSucursalId(int numero, UUID sucursalId);

    @Modifying
    @Query("UPDATE MesaEntity m SET m.estado = :estado WHERE m.id = :id")
    void updateEstado(@Param("id") UUID id, @Param("estado") EstadoMesa estado);
}

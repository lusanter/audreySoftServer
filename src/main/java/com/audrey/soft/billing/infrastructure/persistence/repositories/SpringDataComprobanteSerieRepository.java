package com.audrey.soft.billing.infrastructure.persistence.repositories;

import com.audrey.soft.billing.infrastructure.persistence.entities.ComprobanteSerieEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SpringDataComprobanteSerieRepository extends JpaRepository<ComprobanteSerieEntity, UUID> {

    List<ComprobanteSerieEntity> findBySucursalId(UUID sucursalId);

    Optional<ComprobanteSerieEntity> findByTipoComprobanteAndSucursalIdAndActivoTrue(
            String tipoComprobante, UUID sucursalId);

    // SELECT FOR UPDATE: garantiza que dos cajeros simultáneos no obtengan el mismo correlativo
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM ComprobanteSerieEntity s WHERE s.id = :id")
    Optional<ComprobanteSerieEntity> findByIdWithLock(@Param("id") UUID id);

    @Modifying
    @Query("UPDATE ComprobanteSerieEntity s SET s.correlativoActual = s.correlativoActual + 1 WHERE s.id = :id")
    void incrementarCorrelativo(@Param("id") UUID id);
}

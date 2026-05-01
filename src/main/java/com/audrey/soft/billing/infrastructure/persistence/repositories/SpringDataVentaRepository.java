package com.audrey.soft.billing.infrastructure.persistence.repositories;

import com.audrey.soft.billing.infrastructure.persistence.entities.VentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataVentaRepository extends JpaRepository<VentaEntity, UUID> {

    @Query("SELECT v FROM VentaEntity v WHERE v.sucursal.id = :sucursalId ORDER BY v.createdAt DESC")
    List<VentaEntity> findBySucursalId(@Param("sucursalId") UUID sucursalId);

    @Query(value = """
        SELECT v.* FROM billing.ventas v
        LEFT JOIN fiscal.venta_fiscal vf ON vf.venta_id = v.id
        WHERE v.sucursal_id = :sucursalId
          AND v.created_at >= :desde
          AND v.created_at <= :hasta
          AND (:estado IS NULL OR v.estado = :estado)
          AND (:tipoComprobante IS NULL OR v.tipo_comprobante = :tipoComprobante)
          AND (:serie IS NULL OR v.serie = :serie)
          AND (:fiscalEnviado IS NULL OR vf.enviado = :fiscalEnviado)
        ORDER BY v.created_at DESC
        """, nativeQuery = true)
    List<VentaEntity> findByFiltro(
            @Param("sucursalId") UUID sucursalId,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            @Param("estado") String estado,
            @Param("tipoComprobante") String tipoComprobante,
            @Param("serie") String serie,
            @Param("fiscalEnviado") Boolean fiscalEnviado
    );
}

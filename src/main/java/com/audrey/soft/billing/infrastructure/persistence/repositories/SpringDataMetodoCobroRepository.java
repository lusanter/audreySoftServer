package com.audrey.soft.billing.infrastructure.persistence.repositories;

import com.audrey.soft.billing.infrastructure.persistence.entities.MetodoCobroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface SpringDataMetodoCobroRepository extends JpaRepository<MetodoCobroEntity, UUID> {

    // Devuelve globales (sucursal_id IS NULL) + los específicos de la sucursal
    @Query("SELECT m FROM MetodoCobroEntity m WHERE m.activo = true AND (m.sucursalId IS NULL OR m.sucursalId = :sucursalId)")
    List<MetodoCobroEntity> findDisponiblesBySucursal(@Param("sucursalId") UUID sucursalId);

    @Modifying
    @Transactional
    @Query("UPDATE MetodoCobroEntity m SET m.nombre = :nombre, m.codigo = :codigo, m.imagenUrl = :imagenUrl, m.activo = :activo WHERE m.id = :id")
    void updateFields(@Param("id") UUID id,
                      @Param("nombre") String nombre,
                      @Param("codigo") String codigo,
                      @Param("imagenUrl") String imagenUrl,
                      @Param("activo") boolean activo);
}

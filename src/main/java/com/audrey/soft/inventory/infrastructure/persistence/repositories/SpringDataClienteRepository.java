package com.audrey.soft.inventory.infrastructure.persistence.repositories;

import com.audrey.soft.inventory.infrastructure.persistence.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, UUID> {
    List<ClienteEntity> findBySucursalId(UUID sucursalId);
    boolean existsByDocumentoAndSucursalId(String documento, UUID sucursalId);

    @Query(value = """
        SELECT * FROM inventory.clientes
        WHERE sucursal_id = :sucursalId
          AND (:soloActivos = false OR active = true)
          AND (LOWER(nombre) LIKE LOWER(:nombre))
          AND (documento LIKE :documento)
        ORDER BY nombre ASC
    """, nativeQuery = true)
    List<ClienteEntity> buscar(
            @Param("sucursalId") UUID sucursalId,
            @Param("nombre") String nombre,
            @Param("documento") String documento,
            @Param("soloActivos") boolean soloActivos
    );
}

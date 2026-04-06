package com.audrey.soft.inventory.infrastructure.persistence.repositories;

import com.audrey.soft.inventory.infrastructure.persistence.entities.CategoriaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataCategoriaRepository extends JpaRepository<CategoriaEntity, UUID> {
    List<CategoriaEntity> findBySucursalId(UUID sucursalId);
    boolean existsByNombreAndSucursalId(String nombre, UUID sucursalId);
}

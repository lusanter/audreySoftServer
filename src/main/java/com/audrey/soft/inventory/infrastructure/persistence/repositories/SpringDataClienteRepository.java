package com.audrey.soft.inventory.infrastructure.persistence.repositories;

import com.audrey.soft.inventory.infrastructure.persistence.entities.ClienteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataClienteRepository extends JpaRepository<ClienteEntity, UUID> {
    List<ClienteEntity> findBySucursalId(UUID sucursalId);
    boolean existsByDocumentoAndSucursalId(String documento, UUID sucursalId);
}

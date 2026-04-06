package com.audrey.soft.inventory.domain.ports;

import com.audrey.soft.inventory.domain.models.Cliente;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClienteRepositoryPort {
    Cliente save(Cliente cliente);
    Optional<Cliente> findById(UUID id);
    List<Cliente> findBySucursalId(UUID sucursalId);
    boolean existsByDocumentoAndSucursalId(String documento, UUID sucursalId);
}

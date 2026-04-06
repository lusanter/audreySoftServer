package com.audrey.soft.inventory.domain.ports;

import com.audrey.soft.inventory.domain.models.Categoria;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoriaRepositoryPort {
    Categoria save(Categoria categoria);
    Optional<Categoria> findById(UUID id);
    List<Categoria> findBySucursalId(UUID sucursalId);
    boolean existsByNombreAndSucursalId(String nombre, UUID sucursalId);
}

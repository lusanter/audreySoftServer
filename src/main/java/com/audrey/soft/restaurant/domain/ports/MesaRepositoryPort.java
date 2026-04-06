package com.audrey.soft.restaurant.domain.ports;

import com.audrey.soft.restaurant.domain.models.EstadoMesa;
import com.audrey.soft.restaurant.domain.models.Mesa;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MesaRepositoryPort {
    Mesa save(Mesa mesa);
    Optional<Mesa> findById(UUID id);
    List<Mesa> findBySucursalId(UUID sucursalId);
    boolean existsByNumeroAndSucursalId(int numero, UUID sucursalId);
    void updateEstado(UUID mesaId, EstadoMesa estado);
    void deleteById(UUID id);
}
